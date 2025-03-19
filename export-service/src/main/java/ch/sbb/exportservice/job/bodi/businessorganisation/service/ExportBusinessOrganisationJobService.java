package ch.sbb.exportservice.job.bodi.businessorganisation.service;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.utile.JobDescriptionConstant;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportBusinessOrganisationJobService extends BaseExportJobService {

  protected ExportBusinessOrganisationJobService(
      JobLauncher jobLauncher,
      @Qualifier(JobDescriptionConstant.EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME) Job exportCsvJob,
      @Qualifier(JobDescriptionConstant.EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME) Job exportJsonJob) {
    super(jobLauncher, exportCsvJob, exportJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.FULL),
        new JobParams(ExportTypeV2.ACTUAL),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE)
    );
  }

}
