package ch.sbb.exportservice.service;

import static ch.sbb.atlas.export.enumeration.ExportType.ACTUAL_DATE;
import static ch.sbb.atlas.export.enumeration.ExportType.FULL;
import static ch.sbb.atlas.export.enumeration.ExportType.FUTURE_TIMETABLE;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportTypeV2;
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
        new JobParams(ExportTypeV2.FULL, FULL),
        new JobParams(ExportTypeV2.ACTUAL, ACTUAL_DATE),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, FUTURE_TIMETABLE)
    );
  }
}
