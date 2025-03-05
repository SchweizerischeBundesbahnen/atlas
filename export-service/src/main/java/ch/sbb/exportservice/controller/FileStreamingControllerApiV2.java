package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileExceptionV2;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.service.FileExportService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "Export File Streaming V2")
@RequestMapping("v2/export")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FileStreamingControllerApiV2 {

  private final FileExportService fileExportService;

  @GetMapping(value = "json/{exportObject}/{exportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportJsonFile(
      @PathVariable ExportObjectV2 exportObject,
      @PathVariable ExportTypeV2 exportType) {
    isExportSupported(exportObject, exportType);
    final InputStreamResource body = fileExportService.streamJsonFile(ExportFilePathV2.buildV2(exportObject, exportType));
    return CompletableFuture.supplyAsync(() ->
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "json/latest/{exportObject}/{exportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportJsonFile(
      @PathVariable ExportObjectV2 exportObject,
      @PathVariable ExportTypeV2 exportType) {
    isExportSupported(exportObject, exportType);
    final InputStreamResource body = fileExportService.streamLatestJsonFile(ExportFilePathV2.buildV2(exportObject, exportType));
    return CompletableFuture.supplyAsync(() ->
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "download-gzip-json/{exportObject}/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportGzFile(
      @PathVariable ExportObjectV2 exportObject,
      @PathVariable ExportTypeV2 exportType) {
    isExportSupported(exportObject, exportType);
    final ExportFilePathV2 exportFilePath = ExportFilePathV2.buildV2(exportObject, exportType);
    final HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(exportFilePath.fileName());
    final InputStreamResource body = fileExportService.streamGzipFile(exportFilePath.fileToStream());
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

  @GetMapping(value = "download-gzip-json/latest/{exportObject}/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the latest generated file"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportGzFile(
      @PathVariable ExportObjectV2 exportObject,
      @PathVariable ExportTypeV2 exportType) {
    isExportSupported(exportObject, exportType);
    final String latestUploadedFileName = fileExportService.getLatestUploadedFileName(
        ExportFilePathV2.buildV2(exportObject, exportType));
    final HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(latestUploadedFileName));
    final InputStreamResource body = fileExportService.streamGzipFile(latestUploadedFileName);
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

  private static void isExportSupported(ExportObjectV2 exportObject, ExportTypeV2 exportType) {
    if (!exportObject.isSupportedExportType(exportType)) {
      throw new NotAllowedExportFileExceptionV2(exportObject, exportType);
    }
  }

}
