package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.util.List;
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

@Slf4j
public abstract class BaseExportJobService {

  private final JobLauncher jobLauncher;
  private final Job exportCsvJob;
  private final Job exportJsonJob;

  protected BaseExportJobService(JobLauncher jobLauncher, Job exportCsvJob, Job exportJsonJob) {
    this.jobLauncher = jobLauncher;
    this.exportCsvJob = exportCsvJob;
    this.exportJsonJob = exportJsonJob;
  }

  protected abstract List<ExportType> getExportTypes();

  protected void startExportJob(ExportType exportType, Job job) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, exportType.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(job.getName(), e);
    }
  }

  public void startExportJobs() {
    log.info("Starting export CSV and JSON execution...");
    for (ExportType exportType : getExportTypes()) {
      startExportJob(exportType, exportCsvJob);
      startExportJob(exportType, exportJsonJob);
    }
    log.info("CSV and JSON export execution finished!");
  }

}
