package ch.sbb.exportservice.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.exportservice.job.bodi.businessorganisation.service.ExportBusinessOrganisationJobService;
import ch.sbb.exportservice.job.bodi.transportcompany.service.ExportTransportCompanyJobService;
import ch.sbb.exportservice.job.lidi.line.ExportLineJobService;
import ch.sbb.exportservice.job.lidi.subline.ExportSublineJobService;
import ch.sbb.exportservice.job.lidi.ttfn.ExportTimetableFieldNumberJobService;
import ch.sbb.exportservice.job.prm.contactpoint.ExportContactPointJobService;
import ch.sbb.exportservice.job.prm.parkinglot.ExportParkingLotJobService;
import ch.sbb.exportservice.job.prm.platform.ExportPlatformJobService;
import ch.sbb.exportservice.job.prm.referencepoint.ExportReferencePointJobService;
import ch.sbb.exportservice.job.prm.relation.ExportRelationJobService;
import ch.sbb.exportservice.job.prm.stoppoint.ExportStopPointJobService;
import ch.sbb.exportservice.job.prm.toilet.ExportToiletJobService;
import ch.sbb.exportservice.job.sepodi.loadingpoint.ExportLoadingPointJobService;
import ch.sbb.exportservice.job.sepodi.servicepoint.ExportServicePointJobService;
import ch.sbb.exportservice.job.sepodi.trafficpoint.ExportTrafficPointElementJobService;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
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
    runnableMap.put("bodi/business-organisation-batch", exportBusinessOrganisationJobService::startExportJobs);
    runnableMap.put("bodi/transport-company-batch", exportTransportCompanyJobService::startExportJobs);
    runnableMap.put("prm/stop-point-batch", exportStopPointJobService::startExportJobs);
    runnableMap.put("prm/platform-batch", exportPlatformJobService::startExportJobs);
    runnableMap.put("prm/reference-point-batch", exportReferencePointJobService::startExportJobs);
    runnableMap.put("prm/contact-point-batch", exportContactPointJobService::startExportJobs);
    runnableMap.put("prm/toilet-batch", exportToiletJobService::startExportJobs);
    runnableMap.put("prm/parking-lot-batch", exportParkingLotJobService::startExportJobs);
    runnableMap.put("prm/relation-batch", exportRelationJobService::startExportJobs);
    runnableMap.put("sepodi/service-point-batch", exportServicePointJobService::startExportJobs);
    runnableMap.put("sepodi/traffic-point-batch", exportTrafficPointElementJobService::startExportJobs);
    runnableMap.put("sepodi/loading-point-batch", exportLoadingPointJobService::startExportJobs);
    runnableMap.put("lidi/line-batch", exportLineJobService::startExportJobs);
    runnableMap.put("lidi/subline-batch", exportSublineJobService::startExportJobs);
    runnableMap.put("lidi/ttfn-batch", exportTimetableFieldNumberJobService::startExportJobs);
  }

  @PostMapping("{businessType}/{batchName}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Export started successfully"),
      @ApiResponse(responseCode = "400", description = "Not supported export", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @NewSpan
  @Async
  public CompletableFuture<Void> startExport(@PathVariable String businessType, @PathVariable String batchName) {
    final String operationKey = businessType + "/" + batchName;
    if (!runnableMap.containsKey(operationKey)) {
      throw new BadRequestException("Not supporting export of " + operationKey);
    }
    runnableMap.get(operationKey).run();
    return CompletableFuture.completedFuture(null);
  }

}
