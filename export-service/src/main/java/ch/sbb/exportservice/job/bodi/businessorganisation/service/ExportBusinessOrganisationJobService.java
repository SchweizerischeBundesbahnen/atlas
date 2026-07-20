package ch.sbb.exportservice.job.bodi.businessorganisation.service;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.util.JobDescriptionConstant;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportBusinessOrganisationJobService extends BaseExportJobService {

  protected ExportBusinessOrganisationJobService(
      JobOperator jobOperator,
      @Qualifier(JobDescriptionConstant.EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME) Job exportCsvJob,
      @Qualifier(JobDescriptionConstant.EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME) Job exportJsonJob) {
    super(jobOperator, exportCsvJob, exportJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.BASE;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.BUSINESS_ORGANISATION;
  }

}
