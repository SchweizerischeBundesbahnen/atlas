package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.job.businessorganisation.ExportBusinessOrganisationJobService;
import ch.sbb.exportservice.job.contactpoint.ExportContactPointJobService;
import ch.sbb.exportservice.job.line.ExportLineJobService;
import ch.sbb.exportservice.job.loadingpoint.ExportLoadingPointJobService;
import ch.sbb.exportservice.job.parkinglot.ExportParkingLotJobService;
import ch.sbb.exportservice.job.platform.ExportPlatformJobService;
import ch.sbb.exportservice.job.referencepoint.ExportReferencePointJobService;
import ch.sbb.exportservice.job.relation.ExportRelationJobService;
import ch.sbb.exportservice.job.servicepoint.ExportServicePointJobService;
import ch.sbb.exportservice.job.stoppoint.ExportStopPointJobService;
import ch.sbb.exportservice.job.subline.ExportSublineJobService;
import ch.sbb.exportservice.job.toilet.ExportToiletJobService;
import ch.sbb.exportservice.job.trafficpoint.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.job.transportcompany.ExportTransportCompanyJobService;
import ch.sbb.exportservice.job.ttfn.ExportTimetableFieldNumberJobService;
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
@RequestMapping("v2/export")
@RestController
public class ExportControllerApiV2 {

  private final Map<String, Runnable> runnableMap;

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
    runnableMap.put("sepodi/service-point-batch", exportServicePointJobService::startExportJobs);
    runnableMap.put("sepodi/traffic-point-batch", exportTrafficPointElementJobService::startExportJobs);
    runnableMap.put("sepodi/loading-point-batch", exportLoadingPointJobService::startExportJobs);
    runnableMap.put("lidi/line-batch", exportLineJobService::startExportJobs);
    runnableMap.put("lidi/subline-batch", exportSublineJobService::startExportJobs);
    runnableMap.put("lidi/ttfn-batch", exportTimetableFieldNumberJobService::startExportJobs);
  }

  @PostMapping("{businessType}/{batchName}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @NewSpan
  @Async
  public void startExport(@PathVariable String businessType, @PathVariable String batchName) {
    final String operationKey = businessType + "/" + batchName;
    if (!runnableMap.containsKey(operationKey)) {
      throw new UnsupportedOperationException("Not supporting export of " + operationKey);
    }
    runnableMap.get(operationKey).run();
  }

}
