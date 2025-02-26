package ch.sbb.exportservice.controller;

import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.ExportObjectV1;
import ch.sbb.exportservice.model.ExportTypeV1;
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

@Tag(name = "Export File Streaming")
@RequestMapping(value = {"v1/export", "v1/export/bodi", "v1/export/prm"})
@RestController
@AllArgsConstructor
@Slf4j
public class FileStreamingControllerApiV1 {

  private final FileExportService fileExportService;

  @GetMapping(value = "json/{type}/{subtype}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportJsonFile(
      @PathVariable ExportObjectV1 type,
      @PathVariable ExportTypeV1 subtype) throws NotAllowedExportFileException {
    InputStreamResource body = fileExportService.streamJsonFile(ExportFilePathV1.buildV1(type, subtype));
    return CompletableFuture.supplyAsync(() ->
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "json/latest/{type}/{subtype}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportJsonFile(
      @PathVariable ExportObjectV1 type,
      @PathVariable ExportTypeV1 subtype) throws NotAllowedExportFileException {
    InputStreamResource body = fileExportService.streamLatestJsonFile(ExportFilePathV1.buildV1(type, subtype));
    return CompletableFuture.supplyAsync(() ->
        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body));
  }

  @GetMapping(value = "download-gzip-json/{type}/{subtype}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamExportGzFile(
      @PathVariable ExportObjectV1 type,
      @PathVariable ExportTypeV1 subtype) throws NotAllowedExportFileException {
    final ExportFilePathV1 exportFilePathV1 = ExportFilePathV1.buildV1(type, subtype);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(exportFilePathV1.fileName());
    InputStreamResource body = fileExportService.streamGzipFile(exportFilePathV1.fileToStream());
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

  @GetMapping(value = "download-gzip-json/latest/{type}/{subtype}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the latest generated file"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  @NewSpan
  @Async
  public CompletableFuture<ResponseEntity<InputStreamResource>> streamLatestExportGzFile(
      @PathVariable ExportObjectV1 type,
      @PathVariable ExportTypeV1 subtype) throws NotAllowedExportFileException {
    String latestUploadedFileName = fileExportService.getLatestUploadedFileName(ExportFilePathV1.buildV1(type, subtype));
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(latestUploadedFileName));
    InputStreamResource body = fileExportService.streamGzipFile(latestUploadedFileName);
    return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().headers(headers).body(body));
  }

}
// todo: create V2StreamingController
