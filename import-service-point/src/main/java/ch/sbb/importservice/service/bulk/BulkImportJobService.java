package ch.sbb.importservice.service.bulk;

import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_ID_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.importservice.entity.BulkImport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportJobService {

  private final JobLauncher jobLauncher;
  private final Job bulkImportJob;

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void startBulkImportJob(BulkImport bulkImport, File file) {
    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
            .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
            .addLong(BULK_IMPORT_ID_JOB_PARAMETER, bulkImport.getId())
            .addString(BulkImport.Fields.application, bulkImport.getApplication().toString())
            .addString(BulkImport.Fields.objectType, bulkImport.getObjectType().toString())
            .addString(BulkImport.Fields.importType, bulkImport.getImportType().toString())
            .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis());

    Optional<String> inNameOf = Optional.ofNullable(bulkImport.getInNameOf());
    inNameOf.ifPresent(value -> jobParametersBuilder.addString(BulkImport.Fields.inNameOf, value));

    JobParameters jobParameters = jobParametersBuilder.toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(bulkImportJob, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException | IllegalArgumentException e) {
      throw new JobExecutionException(BULK_IMPORT_JOB_NAME, e);
    } finally {
      deleteFileIfExists(file);
    }
  }

  private static void deleteFileIfExists(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
