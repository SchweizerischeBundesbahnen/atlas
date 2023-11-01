package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportTrafficPointElementJobService extends BaseExportJobService {

  public ExportTrafficPointElementJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME) Job exportTrafficPointElementCsvJob,
      @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME) Job exportTrafficPointElementJsonJob) {
    super(jobLauncher, exportTrafficPointElementCsvJob, exportTrafficPointElementJsonJob);
  }

  @Override
  protected List<ExportTypeBase> getExportTypes() {
    return SePoDiExportType.getWorldOnly();
  }

}
