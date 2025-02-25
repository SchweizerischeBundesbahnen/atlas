package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportTransportCompanyJobService extends BaseExportJobService {

  public ExportTransportCompanyJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME) Job exportTransportCompanyCsvJob,
      @Qualifier(EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME) Job exportTransportCompanyJsonJob) {
    super(jobLauncher, exportTransportCompanyCsvJob, exportTransportCompanyJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportType.FULL, ExportTypeV1.FULL)
    );
  }

}
