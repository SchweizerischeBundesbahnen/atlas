package ch.sbb.exportservice.job.prm.referencepoint.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_REFERENCE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_REFERENCE_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportReferencePointJobService extends BaseExportJobService {

  public ExportReferencePointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_REFERENCE_POINT_CSV_JOB_NAME) Job exportReferencePointCsvJob,
      @Qualifier(EXPORT_REFERENCE_POINT_JSON_JOB_NAME) Job exportReferencePointJsonJob) {
    super(jobLauncher, exportReferencePointCsvJob, exportReferencePointJsonJob);
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
