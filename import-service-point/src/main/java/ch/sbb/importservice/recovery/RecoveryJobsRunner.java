package ch.sbb.importservice.recovery;

import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_TRAFFIC_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.batch.core.StepExecution;
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

  private final ImportProcessedItemRepository importProcessedItemRepository;

  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB_NAME)
  private final Job importServicePointCsvJob;

  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB_NAME)
  private final Job importLoadingPointCsvJob;

  @Qualifier(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME)
  private final Job importTrafficPointCsvJob;

  @Qualifier(IMPORT_STOP_POINT_CSV_JOB_NAME)
  private final Job importStopPointCsvJob;

  @Qualifier(IMPORT_PLATFORM_CSV_JOB_NAME)
  private final Job importPlatformCsvJob;

  private final FileService fileService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Checking jobs to recover...");
    cleanDownloadedFiles();
    recoverJob(IMPORT_SERVICE_POINT_CSV_JOB_NAME);
    recoverJob(IMPORT_LOADING_POINT_CSV_JOB_NAME);
    recoverJob(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME);
    recoverJob(IMPORT_STOP_POINT_CSV_JOB_NAME);
    recoverJob(IMPORT_PLATFORM_CSV_JOB_NAME);
  }

  void recoverJob(String jobName)
      throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {

    log.info("Checking for job {}...", jobName);
    JobInstance jobInstance = jobExplorer.getLastJobInstance(jobName);
    if (jobInstance != null) {
      JobExecution lastJobExecution = jobExplorer.getLastJobExecution(jobInstance);
      if (lastJobExecution != null && lastJobExecution.getStatus().isRunning()) {
        JobParameters jobParameters = getJobParameters(lastJobExecution);
        if (hasJobParameterExecutionBatch(jobParameters)) {
          doRecoverUnfinishedJob(jobName, lastJobExecution, jobParameters);
        } else {
          log.info("No job {} found to recover.", jobName);
        }
      } else {
        log.info("No job {} found to recover.", jobName);
      }
    }
    log.info("No job {} found to recover.", jobName);
  }

  private void doRecoverUnfinishedJob(String jobName, JobExecution lastJobExecution, JobParameters jobParameters)
      throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    log.info("Found a Job status {} to recover...", lastJobExecution.getStatus());
    log.info("Recovering job {} ...", lastJobExecution);
    updateLastJobExecutionStatus(lastJobExecution);
    clearImportedProcessedItem(lastJobExecution);
    JobExecution execution = jobLauncher.run(getJobToRecover(jobName), jobParameters);
    log.info(execution.toString());
  }

  private void cleanDownloadedFiles() {
    log.info("Cleaning downloaded csv files directory..");
    fileService.clearDir();
  }

  private boolean hasJobParameterExecutionBatch(JobParameters jobParameters) {
    Map<String, JobParameter<?>> parameters = jobParameters.getParameters();
    return parameters.containsKey(EXECUTION_TYPE_PARAMETER) && EXECUTION_BATCH_PARAMETER.equals(
        parameters.get(EXECUTION_TYPE_PARAMETER).getValue());
  }

  private void clearImportedProcessedItem(JobExecution lastJobExecution) {
    Optional<StepExecution> stepExecution = lastJobExecution.getStepExecutions().stream().findFirst();
    if(stepExecution.isPresent()) {
      log.info("Clear processedItem from stepExecution: {}", stepExecution);
      importProcessedItemRepository.deleteAllByStepExecutionId(stepExecution.get().getId());
    }
  }

  private void updateLastJobExecutionStatus(JobExecution lastJobExecution) {
    lastJobExecution.setStatus(BatchStatus.ABANDONED);
    lastJobExecution.setExitStatus(new ExitStatus("RECOVERED"));
    jobRepository.update(lastJobExecution);
  }

  private Job getJobToRecover(String jobName) {
    if (IMPORT_SERVICE_POINT_CSV_JOB_NAME.equals(jobName)) {
      return importServicePointCsvJob;
    }
    if (IMPORT_LOADING_POINT_CSV_JOB_NAME.equals(jobName)) {
      return importLoadingPointCsvJob;
    }
    if (IMPORT_TRAFFIC_POINT_CSV_JOB_NAME.equals(jobName)) {
      return importTrafficPointCsvJob;
    }
    if (IMPORT_STOP_POINT_CSV_JOB_NAME.equals(jobName)) {
      return importStopPointCsvJob;
    }
    if (IMPORT_PLATFORM_CSV_JOB_NAME.equals(jobName)) {
      return importPlatformCsvJob;
    }
    throw new IllegalStateException("No job found with name: " + jobName);
  }

  private JobParameters getJobParameters(JobExecution lastJobExecution) {
    JobParameters lastJobExecutionJobParameters = lastJobExecution.getJobParameters();
    Map<String, JobParameter<?>> parameters = lastJobExecutionJobParameters.getParameters();
    JobParameter<?> fullPathFileName = parameters.get(FULL_PATH_FILENAME_JOB_PARAMETER);

    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis());

    if (fullPathFileName != null) {
      jobParametersBuilder.addJobParameter(FULL_PATH_FILENAME_JOB_PARAMETER, fullPathFileName);
    }
    jobParametersBuilder.addJobParameters(lastJobExecutionJobParameters);
    JobParameters jobParameters = jobParametersBuilder.toJobParameters();
    log.info("Run job with parameters {}", jobParameters);
    return jobParameters;
  }
}
