package ch.sbb.exportservice.service;

import static ch.sbb.atlas.export.enumeration.ExportType.ACTUAL_DATE;
import static ch.sbb.atlas.export.enumeration.ExportType.FULL;
import static ch.sbb.atlas.export.enumeration.ExportType.FUTURE_TIMETABLE;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TOILET_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TOILET_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportToiletJobService extends BaseExportJobService {

  public ExportToiletJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_TOILET_CSV_JOB_NAME) Job exportToiletCsvJob,
      @Qualifier(EXPORT_TOILET_JSON_JOB_NAME) Job exportToiletJsonJob) {
    super(jobLauncher, exportToiletCsvJob, exportToiletJsonJob);
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
