package ch.sbb.exportservice.service;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
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
  protected List<ExportTypeBase> getExportTypes() {
    return List.of(ExportType.values());
  }

}
