package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.exportservice.exception.NotAllowedExportFileExceptionV1;
import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
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

@Tag(name = "Export File Streaming V1")
@RequestMapping(value = {"v1/export", "v1/export/prm"})
@RestController
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class FileStreamingControllerApiV1 {

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
      @PathVariable ExportFileName exportFileName,
      @PathVariable ExportTypeBase exportType) {
    isExportSupported(exportFileName, exportType);
    final InputStreamResource body = fileExportService.streamJsonFile(new ExportFilePathV1(exportType, exportFileName));
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
      @PathVariable ExportFileName exportFileName,
      @PathVariable ExportTypeBase exportType) {
    isExportSupported(exportFileName, exportType);
    final InputStreamResource body = fileExportService.streamLatestJsonFile(new ExportFilePathV1(exportType, exportFileName));
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
      @PathVariable ExportFileName exportFileName,
      @PathVariable ExportTypeBase exportType) {
    isExportSupported(exportFileName, exportType);
    final ExportFilePathV1 exportFilePath = new ExportFilePathV1(exportType, exportFileName);
    final HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(exportFilePath.actualDateFileName());
    final InputStreamResource body = fileExportService.streamGzipFile(exportFilePath.fileToStream());
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
      @PathVariable ExportFileName exportFileName,
      @PathVariable ExportTypeBase exportType) {
    isExportSupported(exportFileName, exportType);
    final String latestUploadedFileName = fileExportService.getLatestUploadedFileName(
        new ExportFilePathV1(exportType, exportFileName));
    final HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(latestUploadedFileName));
    final InputStreamResource body = fileExportService.streamGzipFile(latestUploadedFileName);
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

  private static void isExportSupported(ExportFileName exportFileName, ExportTypeBase exportType) {
    switch (exportFileName) {
      case SePoDiBatchExportFileName e -> {
        if (!(exportType instanceof SePoDiExportType)) {
          throw new NotAllowedExportFileExceptionV1(e, SePoDiExportType.class);
        }
      }
      case PrmBatchExportFileName e -> {
        if (!(exportType instanceof PrmExportType)) {
          throw new NotAllowedExportFileExceptionV1(e, PrmExportType.class);
        }
      }
      default -> throw new BadRequestException("Unsupported export type " + exportType);
    }
  }

}
