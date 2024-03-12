package ch.sbb.importservice.controller.user;

import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_DIDOK_USER_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.service.FileHelperService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Import Didok User Batch")
@RequestMapping("v1/import/maintenance")
@RestController
@AllArgsConstructor
@Slf4j
public class ImportDidokUserBatchController {

  private final JobLauncher jobLauncher;
  private final FileHelperService fileHelperService;

  @Qualifier(IMPORT_DIDOK_USER_CSV_JOB_NAME)
  private final Job importDidokUserCsvJob;

  @PostMapping("didok-sepodi-user")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  public ResponseEntity<String> startDidokSePoDiUserImport(@RequestParam("file") MultipartFile multipartFile) throws IOException {
    return execImport(multipartFile, ApplicationType.SEPODI);
  }

  @PostMapping("didok-prm-user")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  public ResponseEntity<String> startDidokPrmUserImport(@RequestParam("file") MultipartFile multipartFile) throws IOException {
    return execImport(multipartFile, ApplicationType.PRM);
  }

  private ResponseEntity<String> execImport(MultipartFile multipartFile, ApplicationType applicationType) throws IOException {
    File file = fileHelperService.getFileFromMultipart(multipartFile);
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addString("applicationType", applicationType.toString())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(importDidokUserCsvJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
      return ResponseEntity.ok().body(execution.toString());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException | IllegalArgumentException e) {
      throw new JobExecutionException(IMPORT_DIDOK_USER_CSV_JOB_NAME, e);
    } finally {
      Files.deleteIfExists(file.toPath());
    }
  }


}
