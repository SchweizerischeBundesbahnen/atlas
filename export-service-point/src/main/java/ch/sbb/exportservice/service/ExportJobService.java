package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ExportJobService {

  public static final String EXPORT_SERVICE_POINT_CSV_JOB = "exportServicePointCsvJob";
  public static final String EXPORT_SERVICE_POINT_JSON_JOB = "exportServicePointJsonJob";

  private final JobLauncher jobLauncher;

  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB)
  private final Job exportServicePointCsvJob;

  @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB)
  private final Job exportServicePointJsonJob;

  public void startExportJobs() {
    log.info("Starting export CSV and JSON execution...");
    for (ServicePointExportType servicePointExportType : ServicePointExportType.values()) {
      startExportJob(servicePointExportType, exportServicePointCsvJob);
      startExportJob(servicePointExportType, exportServicePointJsonJob);
    }
    log.info("CSV and JSON export execution finished!");
  }

  private void startExportJob(ServicePointExportType servicePointExportType, Job job) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, servicePointExportType.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME, e);
    }
  }

}
