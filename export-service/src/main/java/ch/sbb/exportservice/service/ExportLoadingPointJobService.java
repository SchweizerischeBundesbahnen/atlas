package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportTypeV1;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportLoadingPointJobService extends BaseExportJobService {

  public ExportLoadingPointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME) Job exportLoadingPointCsvJob,
      @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME) Job exportLoadingPointJsonJob
  ) {
    super(jobLauncher, exportLoadingPointCsvJob, exportLoadingPointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.WORLD_FULL, ExportTypeV1.WORLD_FULL),
        new JobParams(ExportTypeV2.WORLD_ACTUAL, ExportTypeV1.WORLD_ONLY_ACTUAL),
        new JobParams(ExportTypeV2.WORLD_FUTURE_TIMETABLE, ExportTypeV1.WORLD_ONLY_TIMETABLE_FUTURE)
    );
  }

}
