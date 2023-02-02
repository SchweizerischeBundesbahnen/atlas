package ch.sbb.importservice.contoller;

import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import ch.sbb.importservice.exception.JobExecutionException;
import ch.sbb.importservice.service.FileHelperService;
import java.io.File;
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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@Slf4j
public class ImportServicePointBatchController implements ImportServicePointBatchControllerApiV1 {

  public static final String IMPORT_LOADING_POINT_CSV_JOB = "importLoadingPointCsvJob";
  public static final String IMPORT_SERVICE_POINT_CSV_JOB = "importServicePointCsvJob";
  private final JobLauncher jobLauncher;

  private final FileHelperService fileHelperService;

  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB)
  private final Job importServicePointCsvJob;

  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB)
  private final Job importLoadingPointCsvJob;

  @Override
  @Async
  public void startServicePointImportBatch() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(IMPORT_SERVICE_POINT_CSV_JOB_NAME, e);
    }
  }

  @Override
  public ResponseEntity<?> startServicePointImport(@RequestParam("file") MultipartFile multipartFile) {
    File file = fileHelperService.getFileFromMultipart(multipartFile);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
      return ResponseEntity.ok().body(execution.toString());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException | IllegalArgumentException e) {
      throw new JobExecutionException(IMPORT_SERVICE_POINT_CSV_JOB_NAME, e);
    } finally {
      file.delete();
    }
  }

  @Override
  public ResponseEntity<?> startLoadingPointImport(@RequestParam("file") MultipartFile multipartFile) {
    File file = fileHelperService.getFileFromMultipart(multipartFile);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importLoadingPointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
      return ResponseEntity.ok().body(execution);
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(IMPORT_LOADING_POINT_CSV_JOB_NAME, e);
    } finally {
      file.delete();
    }
  }

}
