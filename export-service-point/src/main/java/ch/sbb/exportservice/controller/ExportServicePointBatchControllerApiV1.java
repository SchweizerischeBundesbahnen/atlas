package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.exception.JobExecutionException;
import ch.sbb.exportservice.repository.ServicePointRepository;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Export Service Point Batch")
@RequestMapping("v1/export")
@RestController
@AllArgsConstructor
@Slf4j
public class ExportServicePointBatchControllerApiV1 {

  public static final String EXPORT_SERVICE_POINT_CSV_JOB = "importServicePointCsvJob";
  private final JobLauncher jobLauncher;

  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB)
  private final Job importServicePointCsvJob;

  private final ServicePointRepository servicePointRepository;

  @PostMapping("service-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  public void startServicePointImportBatch() {
    int count = servicePointRepository.count();
    log.info("count: " + count);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME, e);
    }
  }

}
