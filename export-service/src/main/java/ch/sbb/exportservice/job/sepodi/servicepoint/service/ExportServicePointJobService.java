package ch.sbb.exportservice.job.sepodi.servicepoint.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
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
        new JobParams(ExportTypeV2.SWISS_FULL),
        new JobParams(ExportTypeV2.SWISS_ACTUAL),
        new JobParams(ExportTypeV2.SWISS_FUTURE_TIMETABLE),
        new JobParams(ExportTypeV2.SWISS_TIMETABLE_YEARS),
        new JobParams(ExportTypeV2.WORLD_FULL),
        new JobParams(ExportTypeV2.WORLD_ACTUAL),
        new JobParams(ExportTypeV2.WORLD_FUTURE_TIMETABLE),
        new JobParams(ExportTypeV2.WORLD_TIMETABLE_YEARS)
    );
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.SERVICE_POINT;
  }

}
