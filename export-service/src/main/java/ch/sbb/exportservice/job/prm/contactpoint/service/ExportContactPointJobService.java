package ch.sbb.exportservice.job.prm.contactpoint.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_CONTACT_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_CONTACT_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportContactPointJobService extends BaseExportJobService {

  public ExportContactPointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_CONTACT_POINT_CSV_JOB_NAME) Job exportContactPointCsvJob,
      @Qualifier(EXPORT_CONTACT_POINT_JSON_JOB_NAME) Job exportContactPointJsonJob) {
    super(jobLauncher, exportContactPointCsvJob, exportContactPointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.BASE_WITH_FUTURE;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.CONTACT_POINT;
  }

}
