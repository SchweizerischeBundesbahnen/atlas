package ch.sbb.exportservice.controller;

import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import ch.sbb.exportservice.service.FileExportService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;
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

@Tag(name = "Person with Reduced Mobility - Export")
@RequestMapping("v1/export/prm")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportStopPointBatchControllerApiV1 {

  private final ExportStopPointJobService exportStopPointJobService;

  private final FileExportService<PrmExportType> fileExportService;

  @GetMapping(value = "json/{exportFileName}/{prmExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "Object with filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @Async
  public CompletableFuture<ResponseEntity<StreamingResponseBody>> streamExportJsonFile(@PathVariable PrmBatchExportFileName exportFileName,
      @PathVariable PrmExportType prmExportType) {
    StreamingResponseBody body = fileExportService.streamJsonFile(prmExportType, exportFileName);
    return CompletableFuture.completedFuture(
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "json/latest/{exportFileName}/{prmExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @Async
  public CompletableFuture<ResponseEntity<StreamingResponseBody>> streamLatestExportJsonFile(@PathVariable PrmBatchExportFileName exportFileName,
      @PathVariable PrmExportType prmExportType) {
    String fileName = fileExportService.getLatestUploadedFileName(exportFileName, prmExportType);
    StreamingResponseBody body = fileExportService.streamLatestJsonFile(fileName);
    return CompletableFuture.completedFuture(
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "download-gzip-json/{exportFileName}/{prmExportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file"),
      @ApiResponse(responseCode = "404", description = "No filed found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @Async
  public CompletableFuture<ResponseEntity<StreamingResponseBody>> streamExportGzFile(
      @PathVariable PrmBatchExportFileName exportFileName,
      @PathVariable PrmExportType prmExportType) throws NotAllowedExportFileException {
    String fileName = fileExportService.getBaseFileName(prmExportType, exportFileName);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(fileName);
    StreamingResponseBody body = fileExportService.streamGzipFile(prmExportType, exportFileName);
    return CompletableFuture.completedFuture(ResponseEntity.ok().headers(headers).body(body));
  }

  @GetMapping(value = "download-gzip-json/latest/{exportFileName}/{prmExportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the latest generated file"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @Async
  public CompletableFuture<ResponseEntity<StreamingResponseBody>> streamLatestExportGzFile(
      @PathVariable PrmBatchExportFileName exportFileName,
      @PathVariable PrmExportType prmExportType) throws NotAllowedExportFileException {
    String fileName = fileExportService.getLatestUploadedFileName(exportFileName, prmExportType);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(fileName));
    StreamingResponseBody body = fileExportService.streamGzipFile(prmExportType, exportFileName);
    return CompletableFuture.completedFuture(ResponseEntity.ok().headers(headers).body(body));
  }

  @PostMapping("stop-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportServicePointBatch() {
    exportStopPointJobService.startExportJobs();
  }

}
