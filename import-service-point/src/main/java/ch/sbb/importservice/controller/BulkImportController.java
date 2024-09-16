package ch.sbb.importservice.controller;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BulkImportRequest;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.bulk.BulkImportFileValidationService;
import ch.sbb.importservice.service.bulk.BulkImportService;
import ch.sbb.importservice.service.bulk.template.BulkImportTemplateGenerator;
import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BulkImportController implements BulkImportApiV1 {

  private final BulkImportService bulkImportService;
  private final BulkImportFileValidationService bulkImportFileValidationService;

  @Override
  public void startServicePointImportBatch(BulkImportRequest bulkImportRequest, MultipartFile file) {
    log.info("Starting bulk import:");
    log.info("Application={}, BusinessObject={}, ImportType={}", bulkImportRequest.getApplicationType(), bulkImportRequest.getObjectType(), bulkImportRequest.getImportType());
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
  public ResponseEntity<StreamingResponseBody> downloadTemplate(ApplicationType applicationType, BusinessObjectType objectType,
      ImportType importType) {
    log.info("ApplicationType={}, BusinessObject={}, ImportType={}", applicationType, objectType, importType);
    BulkImportConfig importConfig = BulkImportConfig.builder().application(applicationType).objectType(objectType)
        .importType(importType)
        .build();
    File file = BulkImportTemplateGenerator.generateCsvTemplate(importConfig);

    if (file == null || !file.exists()) {
      log.warn("Unable to generate template file for applicationType={}, objectType={}, importType={}", applicationType,
          objectType, importType);
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(file.length())
        .body(os -> {
              Files.copy(file.toPath(), os);
              Files.delete(file.toPath());
            }
        );
  }

}
