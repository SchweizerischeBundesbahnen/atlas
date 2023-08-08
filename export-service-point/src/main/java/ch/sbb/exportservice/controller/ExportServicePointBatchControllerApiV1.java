package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.exception.NotAllowedExportFileException;
import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Tag(name = "Export Service Point Batch")
@RequestMapping("v1/export")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportServicePointBatchControllerApiV1 {

  private final ExportServicePointJobService exportServicePointJobService;

  private final ExportTrafficPointElementJobService exportTrafficPointElementJobService;

  private final FileExportService fileExportService;

  @GetMapping(value = "json/{exportFileName}/{exportType}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "Object with filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamJsonFile(@PathVariable("exportFileName") ExportFileName exportFileName,
                                                              @PathVariable("exportType") ExportType exportType) {
    checkInputPath(exportFileName,exportType);
    StreamingResponseBody body = fileExportService.streamingJsonFile(exportType,exportFileName);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body);
  }

  @GetMapping(value = "download-gzip-json/{exportFileName}/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<StreamingResponseBody> streamGzipFile(@PathVariable("exportFileName") ExportFileName exportFileName,
                                                              @PathVariable("exportType") ExportType exportType) throws NotAllowedExportFileException {
    checkInputPath(exportFileName,exportType);
    String fileName = fileExportService.getBaseFileName(exportType, exportFileName);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/gzip");
    headers.add("Content-Disposition", "attachment;filename=" + fileName + ".json.gz");
    headers.add("Pragma", "no-cache");
    headers.add("Cache-Control", "no-cache");
    StreamingResponseBody body = fileExportService.streamingGzipFile(exportType, exportFileName);
    return ResponseEntity.ok().headers(headers).body(body);
  }

  @PostMapping("service-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportServicePointJsonBatch() {
    exportServicePointJobService.startExportJobs();
  }

  @PostMapping("traffic-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportTrafficPointElementJsonBatch() {
    exportTrafficPointElementJobService.startExportJobs();
  }

  protected void checkInputPath(ExportFileName exportFileName, ExportType exportType) throws NotAllowedExportFileException {
    if(ExportFileName.TRAFFIC_POINT_ELEMENT_VERSION.equals(exportFileName)){
      if(!ExportType.getWorldOnly().contains(exportType)){
        throw new NotAllowedExportFileException(exportFileName,exportType);
      }
    }
  }

}
