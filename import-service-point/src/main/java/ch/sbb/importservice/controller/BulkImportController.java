package ch.sbb.importservice.controller;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BulkImportRequest;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.bulk.BulkImportFileValidationService;
import ch.sbb.importservice.service.bulk.BulkImportService;
import java.io.File;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Bulk Import")
@RequestMapping("v1/import/bulk")
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
        .inNameOf(bulkImportRequest.getInNameOf() != null ? bulkImportRequest.getInNameOf() : null)
        .build();

    File csvFile = bulkImportFileValidationService.validateFileAndPrepareFile(file, bulkImport.getBulkImportConfig());
    List<String> emails = bulkImportRequest.getEmails() != null ? bulkImportRequest.getEmails() : Collections.emptyList();

    bulkImportService.startBulkImport(bulkImport, csvFile, emails);
  }

  @Override
  public ResponseEntity<Resource> downloadTemplate(BusinessObjectType objectType, ImportType importType) {
    log.info("BusinessObject={}, ImportType={}", objectType, importType);

    File file = bulkImportService.downloadTemplate(objectType, importType);

    if (file == null || !file.exists()) {
      log.warn("Template file not found for objectType={}, importType={}", objectType, importType);
      return ResponseEntity.notFound().build();
    }

    Resource resource = new FileSystemResource(file);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

}
