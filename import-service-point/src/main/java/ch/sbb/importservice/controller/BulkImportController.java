package ch.sbb.importservice.controller;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.BulkImportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Bulk Import")
@RequestMapping("v1/import/bulk")
@RestController
@RequiredArgsConstructor
@Slf4j
public class BulkImportController {

  private final BulkImportService bulkImportService;

  @PostMapping("{application}/{objectType}/{importType}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202"),
  })
  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(#application)""")
  public void startServicePointImportBatch(
      @PathVariable ApplicationType application,
      @PathVariable BusinessObjectType objectType,
      @PathVariable ImportType importType,
      @RequestParam MultipartFile file) {
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
    bulkImportService.startBulkImport(bulkImport, file);
  }

}
