package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_V1_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  protected abstract List<JobParams> getExportTypes();

  protected void startExportJob(JobParams jobParams, Job job) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, jobParams.exportType.toString())
        .addString(EXPORT_TYPE_V1_JOB_PARAMETER, jobParams.exportTypeV1.toString())
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
    for (JobParams jobParams : getExportTypes()) {
      startExportJob(jobParams, exportCsvJob);
      startExportJob(jobParams, exportJsonJob);
    }
    log.info("CSV and JSON export execution finished!");
  }

  @RequiredArgsConstructor
  protected static class JobParams {

    private final ExportType exportType;
    private final ExportTypeV1 exportTypeV1;
  }
}
