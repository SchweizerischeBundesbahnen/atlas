package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.service.ExportContactPointJobService;
import ch.sbb.exportservice.service.ExportParkingLotJobService;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.ExportReferencePointJobService;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import ch.sbb.exportservice.service.ExportToiletJobService;
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

@Tag(name = "Person with Reduced Mobility - Export")
@RequestMapping("v1/export/prm")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportPrmBatchControllerApiV1 {

  private final ExportStopPointJobService exportStopPointJobService;
  private final ExportPlatformJobService exportPlatformJobService;
  private final ExportReferencePointJobService exportReferencePointJobService;
  private final ExportContactPointJobService exportContactPointJobService;
  private final ExportToiletJobService exportToiletJobService;
  private final ExportParkingLotJobService exportParkingLotJobService;


  @PostMapping("stop-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportStopPointBatch() {
    exportStopPointJobService.startExportJobs();
  }

  @PostMapping("platform-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startExportPlatformBatch() {
    exportPlatformJobService.startExportJobs();
  }

  @PostMapping("reference-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportReferencePointBatch() {
    exportReferencePointJobService.startExportJobs();
  }

  @PostMapping("contact-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportContactPointBatch() {
    exportContactPointJobService.startExportJobs();
  }

  @PostMapping("toilet-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportToiletBatch() {
    exportToiletJobService.startExportJobs();
  }

  @PostMapping("parking-lot-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExportParkingLotBatch() {
    exportParkingLotJobService.startExportJobs();
  }

}
