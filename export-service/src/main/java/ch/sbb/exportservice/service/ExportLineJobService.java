package ch.sbb.exportservice.service;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.exportservice.model.ExportTypeV2;
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
    // V1 not needed
    return List.of(
        new JobParams(ExportTypeV2.FULL, ExportType.FULL),
        new JobParams(ExportTypeV2.ACTUAL, ExportType.ACTUAL_DATE),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, ExportType.FUTURE_TIMETABLE)
    );
  }

}
