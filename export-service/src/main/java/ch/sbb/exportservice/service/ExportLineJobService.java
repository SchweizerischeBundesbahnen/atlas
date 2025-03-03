package ch.sbb.exportservice.service;

import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LINE_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LINE_JSON_JOB_NAME;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportLineJobService extends BaseExportJobService {

  public ExportLineJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_LINE_CSV_JOB_NAME) Job exportLoadingPointCsvJob,
      @Qualifier(EXPORT_LINE_JSON_JOB_NAME) Job exportLoadingPointJsonJob
  ) {
    super(jobLauncher, exportLoadingPointCsvJob, exportLoadingPointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportType.FULL, ExportTypeV1.FULL),
        new JobParams(ExportType.ACTUAL, ExportTypeV1.ACTUAL),
        new JobParams(ExportType.FUTURE_TIMETABLE, ExportTypeV1.TIMETABLE_FUTURE)
    );
  }

}
