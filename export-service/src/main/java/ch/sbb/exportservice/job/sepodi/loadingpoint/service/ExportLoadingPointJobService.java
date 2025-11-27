package ch.sbb.exportservice.job.sepodi.loadingpoint.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_LOADING_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportLoadingPointJobService extends BaseExportJobService {

  public ExportLoadingPointJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME) Job exportLoadingPointCsvJob,
      @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME) Job exportLoadingPointJsonJob
  ) {
    super(jobLauncher, exportLoadingPointCsvJob, exportLoadingPointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.WORLD;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.LOADING_POINT;
  }

}
