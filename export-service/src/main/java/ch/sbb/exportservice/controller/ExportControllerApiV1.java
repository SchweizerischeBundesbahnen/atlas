package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.service.ExportBusinessOrganisationJobService;
import ch.sbb.exportservice.service.ExportContactPointJobService;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportParkingLotJobService;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.ExportReferencePointJobService;
import ch.sbb.exportservice.service.ExportRelationJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import ch.sbb.exportservice.service.ExportToiletJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.service.ExportTransportCompanyJobService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Export")
@RequestMapping("v1/export")
@RestController
public class ExportControllerApiV1 {

  private final Map<String, Runnable> runnableMap;

  ExportControllerApiV1(
      ExportBusinessOrganisationJobService exportBusinessOrganisationJobService,
      ExportTransportCompanyJobService exportTransportCompanyJobService,
      ExportStopPointJobService exportStopPointJobService,
      ExportPlatformJobService exportPlatformJobService,
      ExportReferencePointJobService exportReferencePointJobService,
      ExportContactPointJobService exportContactPointJobService,
      ExportToiletJobService exportToiletJobService,
      ExportParkingLotJobService exportParkingLotJobService,
      ExportRelationJobService exportRelationJobService,
      ExportServicePointJobService exportServicePointJobService,
      ExportTrafficPointElementJobService exportTrafficPointElementJobService,
      ExportLoadingPointJobService exportLoadingPointJobService) {
    runnableMap = new HashMap<>();
    runnableMap.put("bodi/business-organisation-batch", exportBusinessOrganisationJobService::startExportJobs);
    runnableMap.put("bodi/transport-company-batch", exportTransportCompanyJobService::startExportJobs);
    runnableMap.put("prm/stop-point-batch", exportStopPointJobService::startExportJobs);
    runnableMap.put("prm/platform-batch", exportPlatformJobService::startExportJobs);
    runnableMap.put("prm/reference-point-batch", exportReferencePointJobService::startExportJobs);
    runnableMap.put("prm/contact-point-batch", exportContactPointJobService::startExportJobs);
    runnableMap.put("prm/toilet-batch", exportToiletJobService::startExportJobs);
    runnableMap.put("prm/parking-lot-batch", exportParkingLotJobService::startExportJobs);
    runnableMap.put("prm/relation-batch", exportRelationJobService::startExportJobs);
    runnableMap.put("service-point-batch", exportServicePointJobService::startExportJobs);
    runnableMap.put("traffic-point-batch", exportTrafficPointElementJobService::startExportJobs);
    runnableMap.put("loading-point-batch", exportLoadingPointJobService::startExportJobs);
  }

  @PostMapping("{batchName}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExport(@PathVariable String batchName) {
    runnableMap.get(batchName).run();
  }

  @PostMapping("{businessType}/{batchName}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExport(@PathVariable String businessType, @PathVariable String batchName) {
    runnableMap.get(businessType + "/" + batchName).run();
  }

}

// todo: add LiDi endpoints