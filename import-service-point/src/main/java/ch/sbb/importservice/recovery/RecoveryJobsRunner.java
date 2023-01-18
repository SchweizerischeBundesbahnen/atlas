package ch.sbb.importservice.recovery;

import static ch.sbb.importservice.config.SpringBatchConfig.IMPORT_LOADING_POINT_CSV_JOB;
import static ch.sbb.importservice.config.SpringBatchConfig.IMPORT_SERVICE_POINT_CSV_JOB;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RecoveryJobsRunner implements ApplicationRunner {

  private final JobExplorer jobExplorer;

  private final JobLauncher jobLauncher;

  private final JobRepository jobRepository;

  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB)
  private final Job importServicePointCsvJob;
  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB)
  private final Job importLoadingPointCsvJob;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Checking jobs to recover...");
    recoverJob(IMPORT_SERVICE_POINT_CSV_JOB);
    recoverJob(IMPORT_LOADING_POINT_CSV_JOB);

  }

  private void recoverJob(String jobName)
      throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    JobInstance jobInstance = jobExplorer.getLastJobInstance(jobName);
    if (jobInstance != null) {
      JobExecution lastJobExecution = jobExplorer.getLastJobExecution(jobInstance);
      if (lastJobExecution != null) {
        if (lastJobExecution.getStatus().isRunning()) {
          log.info("Found a Job status {}", lastJobExecution.getStatus());
          log.info("Recovering job {}", lastJobExecution);
          updateLastJobExecutionStatus(lastJobExecution);
          JobParameters jobParameters = getJobParameters(lastJobExecution);
          JobExecution execution = jobLauncher.run(getJobToRecover(jobName), jobParameters);
          log.info(execution.toString());
        } else {
          log.info("No jobs found to recover.");
        }
      }
    }
    log.info("No jobs found to recover.");
  }

  private void updateLastJobExecutionStatus(JobExecution lastJobExecution) {
    lastJobExecution.setStatus(BatchStatus.ABANDONED);
    lastJobExecution.setExitStatus(new ExitStatus("RECOVERED"));
    jobRepository.update(lastJobExecution);
  }

  private Job getJobToRecover(String jobName) {
    if (IMPORT_SERVICE_POINT_CSV_JOB.equals(jobName)) {
      return importServicePointCsvJob;
    }
    if (IMPORT_LOADING_POINT_CSV_JOB.equals(jobName)) {
      return importLoadingPointCsvJob;
    }
    throw new IllegalStateException("No job found with name: " + jobName);
  }

  private JobParameters getJobParameters(JobExecution lastJobExecution) {
    JobParameters lastJobExecutionJobParameters = lastJobExecution.getJobParameters();
    Map<String, JobParameter> parameters = lastJobExecutionJobParameters.getParameters();
    JobParameter fullPathFileName = parameters.get("fullPathFileName");

    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
        .addLong("startAt", System.currentTimeMillis());

    if (fullPathFileName != null) {
      jobParametersBuilder.addParameter("fullPathFileName", fullPathFileName);
    }
    JobParameters jobParameters = jobParametersBuilder.toJobParameters();
    log.info("Run job with parameters {}", parameters);
    return jobParameters;
  }
}
