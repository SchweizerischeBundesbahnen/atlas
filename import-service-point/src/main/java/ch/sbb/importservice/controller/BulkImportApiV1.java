package ch.sbb.importservice.controller;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Bulk Import")
@RequestMapping("v1/import/bulk")
public interface BulkImportApiV1 {


  @PostMapping("{application}/{objectType}/{importType}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202"),
  })
  void startServicePointImportBatch(
      @PathVariable ApplicationType application,
      @PathVariable BusinessObjectType objectType,
      @PathVariable ImportType importType,
      @RequestParam MultipartFile file);


  @GetMapping("template/{objectType}/{importType}")
  @Operation(summary = "Download bulk import template",
      description = "Downloads a template file for the specified business object type and import type")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Template file successfully downloaded"),
      @ApiResponse(responseCode = "404", description = "Template not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<Resource> downloadTemplate(@PathVariable BusinessObjectType objectType, @PathVariable ImportType importType);

}
