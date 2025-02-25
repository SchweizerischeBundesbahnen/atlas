package ch.sbb.exportservice.service;

import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportBusinessOrganisationJobService extends BaseExportJobService {

  protected ExportBusinessOrganisationJobService(
      JobLauncher jobLauncher,
      @Qualifier(JobDescriptionConstants.EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME) Job exportCsvJob,
      @Qualifier(JobDescriptionConstants.EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME) Job exportJsonJob) {
    super(jobLauncher, exportCsvJob, exportJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportType.FULL, ExportTypeV1.FULL),
        new JobParams(ExportType.ACTUAL, ExportTypeV1.ACTUAL),
        new JobParams(ExportType.FUTURE_TIMETABLE, ExportTypeV1.TIMETABLE_FUTURE)
    );
  }

}
