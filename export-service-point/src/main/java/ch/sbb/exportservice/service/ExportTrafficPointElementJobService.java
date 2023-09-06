package ch.sbb.exportservice.service;

import ch.sbb.exportservice.model.ExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

@Component
public class ExportTrafficPointElementJobService extends BaseExportJobService {

  public ExportTrafficPointElementJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME) Job exportTrafficPointElementCsvJob,
      @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME) Job exportTrafficPointElementJsonJob) {
    super(jobLauncher, exportTrafficPointElementCsvJob, exportTrafficPointElementJsonJob);
  }

  @Override
  protected List<ExportType> getExportTypes() {
    return ExportType.getWorldOnly();
  }

}
