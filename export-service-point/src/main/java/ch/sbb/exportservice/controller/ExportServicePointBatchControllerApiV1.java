package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.service.ExportJobService;
import ch.sbb.exportservice.service.FileExportService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Tag(name = "Export Service Point Batch")
@RequestMapping("v1/export")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportServicePointBatchControllerApiV1 {

  private final ExportJobService exportJobService;

  private final FileExportService fileExportService;

  @GetMapping(value = "json/{servicePointExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "Object with filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamJsonFile(@PathVariable ServicePointExportType servicePointExportType) {
    StreamingResponseBody body = fileExportService.streamingJsonFile(servicePointExportType);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @GetMapping(value = "download-gzip-json/{servicePointExportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamGzipFile(@PathVariable ServicePointExportType servicePointExportType) {
    String fileName = fileExportService.getBaseFileName(servicePointExportType);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/gzip");
    headers.add("Content-Disposition", "attachment;filename=" + fileName + ".json.gz");
    headers.add("Pragma", "no-cache");
    headers.add("Cache-Control", "no-cache");
    StreamingResponseBody body = fileExportService.streamingGzipFile(servicePointExportType);
    return ResponseEntity.ok().headers(headers).body(body);
  }

  @PostMapping("batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportServicePointJsonBatch() {
    exportJobService.startExportJobs();
  }
}
