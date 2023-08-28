package ch.sbb.exportservice.service;

import ch.sbb.exportservice.model.ExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

@Component
public class ExportServicePointJobService extends BaseExportJobService {

  public ExportServicePointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME) Job exportServicePointCsvJob,
      @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME) Job exportServicePointJsonJob) {
    super(jobLauncher, exportServicePointCsvJob, exportServicePointJsonJob);
  }

  @Override
  protected List<ExportType> getExportTypes() {
    return List.of(ExportType.values());
  }

}
