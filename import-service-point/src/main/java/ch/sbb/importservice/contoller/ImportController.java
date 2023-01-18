package ch.sbb.importservice.contoller;

import static ch.sbb.importservice.config.SpringBatchConfig.IMPORT_LOADING_POINT_CSV_JOB;
import static ch.sbb.importservice.config.SpringBatchConfig.IMPORT_SERVICE_POINT_CSV_JOB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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

  private static final String DOCKER_FILE_DIRECTORY = "./tmp";

  private final JobLauncher jobLauncher;

  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB)
  private final Job importServicePointCsvJob;

  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB)
  private final Job importLoadingPointCsvJob;

  @PostMapping("service-point-batch")
  public void startServicePointImportBatch() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("startAt", System.currentTimeMillis()).toJobParameters();

    try {
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {

      e.printStackTrace();
    }
  }

  @PostMapping("loading-point")
  public void startLoadingPointImport()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    File file = new File(
        "C:\\devsbb\\projects\\atlas\\import-service-point\\src\\test\\resources\\DIENSTSTELLEN_V3_IMPORT.csv");

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("startAt", System.currentTimeMillis()).toJobParameters();
    JobExecution execution = jobLauncher.run(importLoadingPointCsvJob, jobParameters);

  }

  @PostMapping("service-point")
  public void startServicePointImport(@RequestParam("file") MultipartFile multipartFile) {
    try {
      String originalFileName = multipartFile.getOriginalFilename();
      File fileToImport = new File(DOCKER_FILE_DIRECTORY + originalFileName);
      multipartFile.transferTo(fileToImport);

      File file = new File(
          "C:\\devsbb\\projects\\atlas\\import-service-point\\src\\test\\resources\\DIENSTSTELLEN_V3_IMPORT.csv");

      //      File file = new File(
      //          "C:\\devsbb\\projects\\atlas\\import-service-point\\src\\test\\resources\\DIENSTSTELLEN_V3_IMPORT.csv");
      //      File file = new File(
      //          "C:\\devsbb\\projects\\atlas\\import-service-point\\src\\test\\resources\\DIDOK3_DIENSTSTELLEN_ALL_V_3_TEST
      //          .csv");

      JobParameters jobParameters = new JobParametersBuilder()
          .addString("fullPathFileName", file.getAbsolutePath())
          .addLong("startAt", System.currentTimeMillis()).toJobParameters();
      JobExecution execution = jobLauncher.run(importServicePointCsvJob, jobParameters);

      if (execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)) {
        //delete the file from the TEMP_STORAGE
        Files.deleteIfExists(Paths.get(DOCKER_FILE_DIRECTORY + originalFileName));
      }

    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException | IOException e) {

      e.printStackTrace();
    }
  }
}
