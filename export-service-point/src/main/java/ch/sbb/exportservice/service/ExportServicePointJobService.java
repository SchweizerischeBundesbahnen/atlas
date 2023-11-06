package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportServicePointJobService extends BaseExportJobService {

  public ExportServicePointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME) Job exportServicePointCsvJob,
      @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME) Job exportServicePointJsonJob) {
    super(jobLauncher, exportServicePointCsvJob, exportServicePointJsonJob);
  }

  @Override
  protected List<ExportTypeBase> getExportTypes() {
    return List.of(SePoDiExportType.values());
  }

}
