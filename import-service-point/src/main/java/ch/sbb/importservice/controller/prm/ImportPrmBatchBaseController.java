package ch.sbb.importservice.controller.prm;

import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.importservice.service.FileHelperService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Slf4j
public abstract class ImportPrmBatchBaseController {

  private final JobLauncher jobLauncher;
  private final FileHelperService fileHelperService;

  void startBatch(Job job, String jobName) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(jobName, e);
    }
  }

  public ResponseEntity<String> startFileImport(Job job, String jobName, MultipartFile multipartFile) throws IOException {
    File file = fileHelperService.getFileFromMultipart(multipartFile);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
      return ResponseEntity.ok().body(execution.toString());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException | IllegalArgumentException e) {
      throw new JobExecutionException(jobName, e);
    } finally {
      Files.delete(file.toPath());
    }
  }

}
