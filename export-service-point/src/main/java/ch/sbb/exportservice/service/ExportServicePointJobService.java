package ch.sbb.exportservice.service;

import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

@Slf4j
@Component
public class ExportServicePointJobService extends BaseExportJobService{

  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  private final Job exportServicePointCsvJob;

  @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME)
  private final Job exportServicePointJsonJob;

  public ExportServicePointJobService(JobLauncher jobLauncher, Job exportServicePointCsvJob, Job exportServicePointJsonJob) {
    super(jobLauncher);
    this.exportServicePointCsvJob = exportServicePointCsvJob;
    this.exportServicePointJsonJob = exportServicePointJsonJob;
  }

  public void startExportJobs() {
    log.info("Starting export CSV and JSON execution...");
    for (ExportType exportType : ExportType.values()) {
      startExportJob(exportType, ExportFileName.SERVICE_POINT_VERSION, exportServicePointCsvJob);
      startExportJob(exportType, ExportFileName.SERVICE_POINT_VERSION, exportServicePointJsonJob);
    }
    log.info("CSV and JSON export execution finished!");
  }

}
