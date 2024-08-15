package ch.sbb.importservice.controller;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.bulk.BulkImportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ch.sbb.importservice.service.BulkImportService;
import java.io.File;
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
  private final FileService fileService;

  @Override
  public void startServicePointImportBatch(ApplicationType application, BusinessObjectType objectType,
      ImportType importType, MultipartFile file) {
    log.info("Starting bulk import:");
    log.info("Application={}, BusinessObject={}, ImportType={}", application, objectType, importType);
    log.info("Uploaded file has size={}, uploadFileName={}, contentType={}",
        FileUtils.byteCountToDisplaySize(file.getSize()),
        file.getOriginalFilename(),
        file.getContentType());

    BulkImport bulkImport = BulkImport.builder()
        .application(application)
        .objectType(objectType)
        .importType(importType)
        .creator(UserService.getUserIdentifier())
        .build();

    bulkImportService.startBulkImport(bulkImport, fileService.getFileFromMultipart(file));
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
