package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.exportservice.job.bodi.businessorganisation.service.ExportBusinessOrganisationJobService;
import ch.sbb.exportservice.job.bodi.transportcompany.service.ExportTransportCompanyJobService;
import ch.sbb.exportservice.job.lidi.line.service.ExportLineJobService;
import ch.sbb.exportservice.job.lidi.subline.service.ExportSublineJobService;
import ch.sbb.exportservice.job.lidi.ttfn.service.ExportTimetableFieldNumberJobService;
import ch.sbb.exportservice.job.prm.contactpoint.service.ExportContactPointJobService;
import ch.sbb.exportservice.job.prm.parkinglot.service.ExportParkingLotJobService;
import ch.sbb.exportservice.job.prm.platform.service.ExportPlatformJobService;
import ch.sbb.exportservice.job.prm.referencepoint.service.ExportReferencePointJobService;
import ch.sbb.exportservice.job.prm.relation.service.ExportRelationJobService;
import ch.sbb.exportservice.job.prm.stoppoint.service.ExportStopPointJobService;
import ch.sbb.exportservice.job.prm.toilet.service.ExportToiletJobService;
import ch.sbb.exportservice.job.sepodi.loadingpoint.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.job.sepodi.servicepoint.service.ExportServicePointJobService;
import ch.sbb.exportservice.job.sepodi.trafficpoint.service.ExportTrafficPointElementJobService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Export")
@RequestMapping("v2/export")
@RestController
public class ExportControllerApiV2 {

  private final Map<String, Runnable> runnableMap = new HashMap<>();

  ExportControllerApiV2(
      ExportBusinessOrganisationJobService exportBusinessOrganisationJobService,
      ExportTimetableFieldNumberJobService exportTimetableFieldNumberJobService,
      ExportLineJobService exportLineJobService,
      ExportSublineJobService exportSublineJobService,
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
    runnableMap.put("bodi/business-organisation-batch", exportBusinessOrganisationJobService::startExportJobsAsync);
    runnableMap.put("bodi/transport-company-batch", exportTransportCompanyJobService::startExportJobsAsync);
    runnableMap.put("prm/stop-point-batch", exportStopPointJobService::startExportJobsAsync);
    runnableMap.put("prm/platform-batch", exportPlatformJobService::startExportJobsAsync);
    runnableMap.put("prm/reference-point-batch", exportReferencePointJobService::startExportJobsAsync);
    runnableMap.put("prm/contact-point-batch", exportContactPointJobService::startExportJobsAsync);
    runnableMap.put("prm/toilet-batch", exportToiletJobService::startExportJobsAsync);
    runnableMap.put("prm/parking-lot-batch", exportParkingLotJobService::startExportJobsAsync);
    runnableMap.put("prm/relation-batch", exportRelationJobService::startExportJobsAsync);
    runnableMap.put("sepodi/service-point-batch", exportServicePointJobService::startExportJobsAsync);
    runnableMap.put("sepodi/traffic-point-batch", exportTrafficPointElementJobService::startExportJobsAsync);
    runnableMap.put("sepodi/loading-point-batch", exportLoadingPointJobService::startExportJobsAsync);
    runnableMap.put("lidi/line-batch", exportLineJobService::startExportJobsAsync);
    runnableMap.put("lidi/subline-batch", exportSublineJobService::startExportJobsAsync);
    runnableMap.put("lidi/ttfn-batch", exportTimetableFieldNumberJobService::startExportJobsAsync);
  }

  @PostMapping("{businessType}/{batchName}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Export started successfully"),
      @ApiResponse(responseCode = "400", description = "Not supported export", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @NewSpan
  public ResponseEntity<Void> startExport(@PathVariable String businessType, @PathVariable String batchName) {
    final String operationKey = businessType + "/" + batchName;
    if (!runnableMap.containsKey(operationKey)) {
      throw new BadRequestException("Not supporting export of " + operationKey);
    }
    runnableMap.get(operationKey).run();
    return ResponseEntity.ok().build();
  }

}
