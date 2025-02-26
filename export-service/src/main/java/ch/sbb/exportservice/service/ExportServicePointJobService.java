package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.SePoDiExportType;
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
        new JobParams(ExportTypeV2.SWISS_FULL, SePoDiExportType.SWISS_ONLY_FULL),
        new JobParams(ExportTypeV2.SWISS_ACTUAL, SePoDiExportType.SWISS_ONLY_ACTUAL),
        new JobParams(ExportTypeV2.SWISS_FUTURE_TIMETABLE, SePoDiExportType.SWISS_ONLY_TIMETABLE_FUTURE),
        new JobParams(ExportTypeV2.WORLD_FULL, SePoDiExportType.WORLD_FULL),
        new JobParams(ExportTypeV2.WORLD_ACTUAL, SePoDiExportType.WORLD_ONLY_ACTUAL),
        new JobParams(ExportTypeV2.WORLD_FUTURE_TIMETABLE, SePoDiExportType.WORLD_ONLY_TIMETABLE_FUTURE)
    );
  }

}
