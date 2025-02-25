package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportServicePointJobService extends BaseExportJobService {

  public ExportServicePointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME) Job exportServicePointCsvJob,
      @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME) Job exportServicePointJsonJob) {
    super(jobLauncher, exportServicePointCsvJob, exportServicePointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportType.SWISS_FULL, ExportTypeV1.SWISS_ONLY_FULL),
        new JobParams(ExportType.SWISS_ACTUAL, ExportTypeV1.SWISS_ONLY_ACTUAL),
        new JobParams(ExportType.SWISS_FUTURE_TIMETABLE, ExportTypeV1.SWISS_ONLY_TIMETABLE_FUTURE),
        new JobParams(ExportType.WORLD_FULL, ExportTypeV1.WORLD_FULL),
        new JobParams(ExportType.WORLD_ACTUAL, ExportTypeV1.WORLD_ONLY_ACTUAL),
        new JobParams(ExportType.WORLD_FUTURE_TIMETABLE, ExportTypeV1.WORLD_ONLY_TIMETABLE_FUTURE)
    );
  }

}
