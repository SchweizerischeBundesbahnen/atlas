package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.controller.GzipFileDownloadHttpHeader;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.ExportStopPointJobService;
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

@Tag(name = "SePoDi - Export")
@RequestMapping("v1/export/prm")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportStopPointBatchControllerApiV1 {

  private final ExportStopPointJobService exportStopPointJobService;

  private final FileExportService fileExportService;

  @GetMapping(value = "json/{exportFileName}/{exportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "Object with filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamExportJsonFile(@PathVariable BatchExportFileName exportFileName,
                                                                    @PathVariable ExportType exportType) {
    checkInputPath(exportFileName,exportType);
    StreamingResponseBody body = fileExportService.streamJsonFile(exportType,exportFileName);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @GetMapping(value = "download-gzip-json/{exportFileName}/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamExportGzFile(
      @PathVariable BatchExportFileName exportFileName,
      @PathVariable ExportType exportType) throws NotAllowedExportFileException {
    checkInputPath(exportFileName, exportType);
    String fileName = fileExportService.getBaseFileName(exportType, exportFileName);
    HttpHeaders headers = GzipFileDownloadHttpHeader.getHeaders(fileName);
    StreamingResponseBody body = fileExportService.streamGzipFile(exportType, exportFileName);
    return ResponseEntity.ok().headers(headers).body(body);
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

  private void checkInputPath(BatchExportFileName exportFileName, ExportType exportType) {
    final List<BatchExportFileName> worldOnlyTypes = List.of(
        BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION,
        BatchExportFileName.LOADING_POINT_VERSION
    );
    if (worldOnlyTypes.contains(exportFileName) && !ExportType.getWorldOnly().contains(exportType)) {
      throw new NotAllowedExportFileException(exportFileName, exportType);
    }
  }

}
