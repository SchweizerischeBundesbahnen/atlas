package ch.sbb.exportservice.job.prm.toilet.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TOILET_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TOILET_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
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
        new JobParams(ExportTypeV2.FULL, PrmExportType.FULL),
        new JobParams(ExportTypeV2.ACTUAL, PrmExportType.ACTUAL),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, PrmExportType.TIMETABLE_FUTURE)
    );
  }

}
