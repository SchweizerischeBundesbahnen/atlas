package ch.sbb.exportservice.config;

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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExportServiceConfig {

  private final ExportBusinessOrganisationJobService exportBusinessOrganisationJobService;
  private final ExportTimetableFieldNumberJobService exportTimetableFieldNumberJobService;
  private final ExportLineJobService exportLineJobService;
  private final ExportSublineJobService exportSublineJobService;
  private final ExportTransportCompanyJobService exportTransportCompanyJobService;
  private final ExportStopPointJobService exportStopPointJobService;
  private final ExportPlatformJobService exportPlatformJobService;
  private final ExportReferencePointJobService exportReferencePointJobService;
  private final ExportContactPointJobService exportContactPointJobService;
  private final ExportToiletJobService exportToiletJobService;
  private final ExportParkingLotJobService exportParkingLotJobService;
  private final ExportRelationJobService exportRelationJobService;
  private final ExportServicePointJobService exportServicePointJobService;
  private final ExportTrafficPointElementJobService exportTrafficPointElementJobService;
  private final ExportLoadingPointJobService exportLoadingPointJobService;

  @Bean
  public Map<String, Runnable> exportServiceOperations() {
    return Map.ofEntries(
        Map.entry("bodi/business-organisation-batch", exportBusinessOrganisationJobService::startExportJobsAsync),
        Map.entry("bodi/transport-company-batch", exportTransportCompanyJobService::startExportJobsAsync),
        Map.entry("prm/stop-point-batch", exportStopPointJobService::startExportJobsAsync),
        Map.entry("prm/platform-batch", exportPlatformJobService::startExportJobsAsync),
        Map.entry("prm/reference-point-batch", exportReferencePointJobService::startExportJobsAsync),
        Map.entry("prm/contact-point-batch", exportContactPointJobService::startExportJobsAsync),
        Map.entry("prm/toilet-batch", exportToiletJobService::startExportJobsAsync),
        Map.entry("prm/parking-lot-batch", exportParkingLotJobService::startExportJobsAsync),
        Map.entry("prm/relation-batch", exportRelationJobService::startExportJobsAsync),
        Map.entry("sepodi/service-point-batch", exportServicePointJobService::startExportJobsAsync),
        Map.entry("sepodi/traffic-point-batch", exportTrafficPointElementJobService::startExportJobsAsync),
        Map.entry("sepodi/loading-point-batch", exportLoadingPointJobService::startExportJobsAsync),
        Map.entry("lidi/line-batch", exportLineJobService::startExportJobsAsync),
        Map.entry("lidi/subline-batch", exportSublineJobService::startExportJobsAsync),
        Map.entry("lidi/ttfn-batch", exportTimetableFieldNumberJobService::startExportJobsAsync)
    );
  }

}
