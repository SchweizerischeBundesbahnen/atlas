package ch.sbb.exportservice.job.bodi.transportcompany;

import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME;
import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
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
        new JobParams(ExportTypeV2.FULL)
    );
  }

}
