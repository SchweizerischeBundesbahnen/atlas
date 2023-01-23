package ch.sbb.importservice.contoller;

import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import ch.sbb.atlas.base.service.amazon.service.FileService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/import")
@AllArgsConstructor
@Slf4j
public class ImportController {

  private static final String IMPORT_SERVICE_POINT_CSV_JOB = "importServicePointCsvJob";
  private static final String IMPORT_LOADING_POINT_CSV_JOB = "importLoadingPointCsvJob";

  private static final String DOCKER_FILE_DIRECTORY = "./tmp";

  private final JobLauncher jobLauncher;

  private final FileService fileService;

  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB)
  private final Job importServicePointCsvJob;

  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB)
  private final Job importLoadingPointCsvJob;

  @PostMapping("service-point-batch")
  public void startServicePointImportBatch() {

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    try {
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("loading-point")
  public void startLoadingPointImport(@RequestParam("file") MultipartFile multipartFile) {
    File file = getFileFromMultipart(multipartFile);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importLoadingPointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new RuntimeException(e);
    } finally {
      file.delete();
    }
  }

  @PostMapping("service-point")
  public void startServicePointImport(@RequestParam("file") MultipartFile multipartFile) {
    File file = getFileFromMultipart(multipartFile);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new RuntimeException(e);
    } finally {
      file.delete();
    }
  }

  File getFileFromMultipart(MultipartFile multipartFile) {
    String dir = fileService.getDir();
    String originalFileName = multipartFile.getOriginalFilename();
    File fileToImport = new File(dir + File.separator + originalFileName);
    try (OutputStream os = new FileOutputStream(fileToImport)) {
      os.write(multipartFile.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileToImport;
  }
}
