package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportStopPointJobService extends BaseExportJobService {

  public ExportStopPointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_STOP_POINT_CSV_JOB_NAME) Job exportStopPointCsvJob,
      @Qualifier(EXPORT_STOP_POINT_JSON_JOB_NAME) Job exportStopPointJsonJob) {
    super(jobLauncher, exportStopPointCsvJob, exportStopPointJsonJob);
  }

  @Override
  protected List<ExportType> getExportTypes() {
    return List.of(ExportType.values());
  }

}
