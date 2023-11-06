package ch.sbb.exportservice.controller;

import static ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader.extractFileNameFromS3ObjectName;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.service.FileExportService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

  private final ExportServicePointJobService exportServicePointJobService;
  private final ExportTrafficPointElementJobService exportTrafficPointElementJobService;
  private final ExportLoadingPointJobService exportLoadingPointJobService;

  private final FileExportService<SePoDiExportType> fileExportService;

  @GetMapping(value = "json/{exportFileName}/{sePoDiExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamExportJsonFile(@PathVariable SePoDiBatchExportFileName exportFileName,
      @PathVariable SePoDiExportType sePoDiExportType) {
    checkInputPath(exportFileName, sePoDiExportType);
    StreamingResponseBody body = fileExportService.streamJsonFile(sePoDiExportType, exportFileName);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @GetMapping(value = "json/latest/{exportFileName}/{sePoDiExportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",description = "Returns the today generated file as Stream"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamLatestExportJsonFile(@PathVariable SePoDiBatchExportFileName exportFileName,
                                                                    @PathVariable SePoDiExportType sePoDiExportType) {
    checkInputPath(exportFileName, sePoDiExportType);
    String fileName = fileExportService.getLatestUploadedFileName(exportFileName, sePoDiExportType);
    StreamingResponseBody body = fileExportService.streamLatestJsonFile(fileName);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @GetMapping(value = "download-gzip-json/{exportFileName}/{sePoDiExportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the today generated file"),
      @ApiResponse(responseCode = "404", description = "No file found for today date", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamExportGzFile(
      @PathVariable SePoDiBatchExportFileName exportFileName,
      @PathVariable SePoDiExportType sePoDiExportType) throws NotAllowedExportFileException {
    checkInputPath(exportFileName, sePoDiExportType);
    String fileName = fileExportService.getBaseFileName(sePoDiExportType, exportFileName);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(fileName);
    StreamingResponseBody body = fileExportService.streamGzipFile(sePoDiExportType, exportFileName);
    return ResponseEntity.ok().headers(headers).body(body);
  }

  @GetMapping(value = "download-gzip-json/latest/{exportFileName}/{sePoDiExportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the latest generated file"),
      @ApiResponse(responseCode = "404", description = "No generated files found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamLatestExportGzFile(
      @PathVariable SePoDiBatchExportFileName exportFileName,
      @PathVariable SePoDiExportType sePoDiExportType) throws NotAllowedExportFileException {
    checkInputPath(exportFileName, sePoDiExportType);
    String fileName = fileExportService.getLatestUploadedFileName(exportFileName, sePoDiExportType);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(extractFileNameFromS3ObjectName(fileName));
    StreamingResponseBody body = fileExportService.streamLatestGzipFile(fileName);
    return ResponseEntity.ok().headers(headers).body(body);
  }

  @PostMapping("service-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportServicePointBatch() {
    exportServicePointJobService.startExportJobs();
  }

  @PostMapping("traffic-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportTrafficPointElementBatch() {
    exportTrafficPointElementJobService.startExportJobs();
  }

  @PostMapping("loading-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportLoadingPointBatch() {
    exportLoadingPointJobService.startExportJobs();
  }

  private void checkInputPath(SePoDiBatchExportFileName exportFileName, SePoDiExportType sePoDiExportType) {
    final List<SePoDiBatchExportFileName> worldOnlyTypes = List.of(
        SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION,
        SePoDiBatchExportFileName.LOADING_POINT_VERSION
    );
    if (worldOnlyTypes.contains(exportFileName) && !SePoDiExportType.getWorldOnly().contains(sePoDiExportType)) {
      throw new NotAllowedExportFileException(exportFileName, sePoDiExportType);
    }
  }

}
