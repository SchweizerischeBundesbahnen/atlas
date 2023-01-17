package ch.sbb.importservice;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
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

  @Qualifier("importServicePointCsvJob")
  private final Job importServicePointCsvJob;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Checking jobs to recover...");
    JobInstance jobInstance = jobExplorer.getLastJobInstance("importServicePointCsv");
    if (jobInstance != null) {
      JobExecution lastJobExecution = jobExplorer.getLastJobExecution(jobInstance);
      if (lastJobExecution != null) {
        if (lastJobExecution.getStatus().isRunning()) {
          log.info("Found a Job status {}", lastJobExecution.getStatus());
          log.info("Recovering job {}", lastJobExecution);
          JobParameters lastJobExecutionJobParameters = lastJobExecution.getJobParameters();
          Map<String, JobParameter> parameters = lastJobExecutionJobParameters.getParameters();
          JobParameter fullPathFileName = parameters.get("fullPathFileName");
          JobParameters jobParameters = new JobParametersBuilder()
              .addParameter("fullPathFileName", fullPathFileName)
              .addLong("startAt", System.currentTimeMillis())
              .toJobParameters();
          log.info("Run job with parameters {}", parameters);
          JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
          log.info(execution.toString());
        } else {
          log.info("No jobs found to recover.");
        }
      }
    }

  }
}
