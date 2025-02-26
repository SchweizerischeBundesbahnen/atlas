package ch.sbb.exportservice.service;

import static ch.sbb.atlas.export.enumeration.ExportType.ACTUAL_DATE;
import static ch.sbb.atlas.export.enumeration.ExportType.FULL;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.exportservice.model.ExportTypeV2;
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
        new JobParams(ExportTypeV2.FULL, FULL), // todo: does not need v1
        new JobParams(ExportTypeV2.ACTUAL, ACTUAL_DATE),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, ExportType.FUTURE_TIMETABLE)
    );
  }

}
