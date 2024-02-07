package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Export Service Point Batch")
@RequestMapping("v1/export")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportServicePointBatchControllerApiV1 {

  private final ExportServicePointJobService exportServicePointJobService;
  private final ExportTrafficPointElementJobService exportTrafficPointElementJobService;
  private final ExportLoadingPointJobService exportLoadingPointJobService;

  @PostMapping("service-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportServicePointBatch() {
    exportServicePointJobService.startExportJobs();
  }

  @PostMapping("traffic-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportTrafficPointElementBatch() {
    exportTrafficPointElementJobService.startExportJobs();
  }

  @PostMapping("loading-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportLoadingPointBatch() {
    exportLoadingPointJobService.startExportJobs();
  }

}
