package ch.sbb.exportservice.job.prm.platform.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_PLATFORM_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportPlatformJobService extends BaseExportJobService {

  public ExportPlatformJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_PLATFORM_CSV_JOB_NAME) Job exportPlatformCsvJob,
      @Qualifier(EXPORT_PLATFORM_JSON_JOB_NAME) Job exportPlatformJsonJob) {
    super(jobLauncher, exportPlatformCsvJob, exportPlatformJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.FULL, PrmExportType.FULL),
        new JobParams(ExportTypeV2.ACTUAL, PrmExportType.ACTUAL),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, PrmExportType.TIMETABLE_FUTURE)
    );
  }
}
