package ch.sbb.importservice.controller;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.exception.LogFileNotFoundException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BulkImportRequest;
import ch.sbb.importservice.model.BulkImportResult;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.bulk.BulkImportFileValidationService;
import ch.sbb.importservice.service.bulk.BulkImportService;
import ch.sbb.importservice.service.bulk.log.BulkImportLogService;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.bulk.template.BulkImportTemplateGenerator;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BulkImportController implements BulkImportApiV1 {

  private final BulkImportService bulkImportService;
  private final BulkImportLogService bulkImportLogService;
  private final BulkImportFileValidationService bulkImportFileValidationService;
  private final BulkImportTemplateGenerator bulkImportTemplateGenerator;

  @Override
  public void startBulkImport(BulkImportRequest bulkImportRequest, MultipartFile file) {
    log.info("Starting bulk import:");
    log.info("Application={}, BusinessObject={}, ImportType={}", bulkImportRequest.getApplicationType(),
        bulkImportRequest.getObjectType(), bulkImportRequest.getImportType());
    log.info("Uploaded file has size={}, uploadFileName={}, contentType={}",
        FileUtils.byteCountToDisplaySize(file.getSize()),
        file.getOriginalFilename(),
        file.getContentType());

    BulkImport bulkImport = BulkImport.builder()
        .application(bulkImportRequest.getApplicationType())
        .objectType(bulkImportRequest.getObjectType())
        .importType(bulkImportRequest.getImportType())
        .creator(UserService.getUserIdentifier())
        .inNameOf(bulkImportRequest.getInNameOf())
        .build();

    File csvFile = bulkImportFileValidationService.validateFileAndPrepareFile(file, bulkImport.getBulkImportConfig());
    List<String> emails = bulkImportRequest.getEmails() != null ? bulkImportRequest.getEmails() : Collections.emptyList();

    bulkImportService.startBulkImport(bulkImport, csvFile, emails);
  }

  @Override
  public ResponseEntity<Resource> downloadTemplate(ApplicationType applicationType, BusinessObjectType objectType,
      ImportType importType) {
    log.info("ApplicationType={}, BusinessObject={}, ImportType={}", applicationType, objectType, importType);
    BulkImportConfig importConfig = BulkImportConfig.builder().application(applicationType).objectType(objectType)
        .importType(importType)
        .build();
    File file = bulkImportTemplateGenerator.generateCsvTemplate(importConfig);

    if (file == null || !file.exists()) {
      log.warn("Unable to generate template file for applicationType={}, objectType={}, importType={}", applicationType,
          objectType, importType);
      return ResponseEntity.notFound().build();
    }
    Resource resource = getDeletableResource(file);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(file.length())
        .body(resource);
  }

  @Override
  public BulkImportResult getBulkImportResults(Long id) {
    BulkImport bulkImport = bulkImportService.getBulkImport(id);
    String logFileUrl = bulkImport.getLogFileUrl();
    if (logFileUrl == null) {
      throw new LogFileNotFoundException(bulkImport);
    }
    LogFile logFile = bulkImportLogService.getLogFileFromS3(logFileUrl);

    return BulkImportResult.builder()
        .businessObjectType(bulkImport.getObjectType())
        .creationDate(bulkImport.getCreationDate())
        .creator(bulkImport.getCreator())
        .inNameOf(bulkImport.getInNameOf())
        .importType(bulkImport.getImportType())
        .logEntries(logFile.getLogEntries())
        .nbOfSuccess(logFile.getNbOfSuccess())
        .nbOfInfo(logFile.getNbOfInfo())
        .nbOfError(logFile.getNbOfError())
        .build();
  }

  private static Resource getDeletableResource(File file) {
    return new FileSystemResource(file) {
      @Override
      public @NotNull InputStream getInputStream() throws IOException {
        return new FileInputStream(file) {
          @Override
          public void close() throws IOException {
            super.close();
            Files.delete(file.toPath());
          }
        };
      }
    };
  }

}
