package ch.sbb.importservice.controller;

import static ch.sbb.importservice.config.ServicePointGeoLocationUpdateConfig.UPDATE_SERVICE_POINT_GEO_JOB;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ServicePointUpdateGeoController implements ServicePointUpdateGeoApiV1 {

  private final JobLauncher jobLauncher;

  @Qualifier("updateServicePointGeoJob")
  private final Job updateServicePointGeoJob;

  @Override
  public void startServicePointImportBatch() throws JobExecutionException {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(updateServicePointGeoJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException | IllegalArgumentException e) {
      throw new JobExecutionException(UPDATE_SERVICE_POINT_GEO_JOB, e);
    }
  }
}
