package ch.sbb.exportservice.controller;

import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.BoDiBatchExportFileName;
import ch.sbb.exportservice.model.BoDiExportType;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.service.FileExportService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Business Organisations - Export")
@RequestMapping("v1/export/bodi")
@RestController
@AllArgsConstructor
@Slf4j
public class BoDiFileStreamingControllerApiV1 {

  private final FileExportService fileExportService;

  @GetMapping(value = "json/{exportFileName}/{exportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportJsonFile(
      @PathVariable BoDiBatchExportFileName exportFileName,
      @PathVariable BoDiExportType exportType) {
    InputStreamResource body = fileExportService.streamJsonFile(exportType, exportFileName);
    return CompletableFuture.supplyAsync(() ->
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "json/latest/{exportFileName}/{exportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportJsonFile(
      @PathVariable BoDiBatchExportFileName exportFileName,
      @PathVariable BoDiExportType exportType) {
    InputStreamResource body = fileExportService.streamLatestJsonFile(exportType, exportFileName);
    return CompletableFuture.supplyAsync(() ->
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "download-gzip-json/{exportFileName}/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportGzFile(
      @PathVariable BoDiBatchExportFileName exportFileName,
      @PathVariable BoDiExportType exportType) throws NotAllowedExportFileException {
    ExportFilePath exportFilePath = new ExportFilePath(exportType, exportFileName);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(exportFilePath.actualDateFileName());
    InputStreamResource body = fileExportService.streamGzipFile(exportFilePath.fileToStream());
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

  @GetMapping(value = "download-gzip-json/latest/{exportFileName}/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the latest generated file"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportGzFile(
      @PathVariable BoDiBatchExportFileName exportFileName,
      @PathVariable BoDiExportType exportType) throws NotAllowedExportFileException {
    String latestUploadedFileName = fileExportService.getLatestUploadedFileName(exportType, exportFileName);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(latestUploadedFileName));
    InputStreamResource body = fileExportService.streamGzipFile(latestUploadedFileName);
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

}
