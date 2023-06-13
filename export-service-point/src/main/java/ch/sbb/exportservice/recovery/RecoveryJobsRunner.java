package ch.sbb.exportservice.recovery;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RecoveryJobsRunner {

}
//implements ApplicationRunner {
//
//  private final JobExplorer jobExplorer;
//
//  private final JobLauncher jobLauncher;
//
//  private final JobRepository jobRepository;
//
//  @Qualifier(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)
//  private final Job importServicePointCsvJob;
//
//  @Qualifier(JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME)
//  private final Job importServicePointJsonJob;
//  private final FileService fileService;
//
//  @Override
//  public void run(ApplicationArguments args) throws Exception {
//    log.info("Checking jobs to recover...");
//    cleanDownloadedFiles();
//    recoverJob(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME);
//    recoverJob(JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME);
//  }
//
//  void recoverJob(String jobName)
//      throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
//      JobParametersInvalidException {
//
//    log.info("Checking for job {}...", jobName);
//    JobInstance jobInstance = jobExplorer.getLastJobInstance(jobName);
//    if (jobInstance != null) {
//      JobExecution lastJobExecution = jobExplorer.getLastJobExecution(jobInstance);
//      if (lastJobExecution != null && lastJobExecution.getStatus().isRunning()) {
//        JobParameters jobParameters = getJobParameters(lastJobExecution);
//        if (hasJobParameterExecutionBatch(jobParameters)) {
//          doRevoverUnfinishedJob(jobName, lastJobExecution, jobParameters);
//        } else {
//          log.info("No job {} found to recover.", jobName);
//        }
//      } else {
//        log.info("No job {} found to recover.", jobName);
//      }
//    }
//    log.info("No job {} found to recover.", jobName);
//  }
//
//  private void doRevoverUnfinishedJob(String jobName, JobExecution lastJobExecution, JobParameters jobParameters)
//      throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
//      JobParametersInvalidException {
//    log.info("Found a Job status {} to recover...", lastJobExecution.getStatus());
//    log.info("Recovering job {} ...", lastJobExecution);
//    updateLastJobExecutionStatus(lastJobExecution);
//    clearImportedProcessedItem(lastJobExecution);
//    JobExecution execution = jobLauncher.run(getJobToRecover(jobName), jobParameters);
//    log.info(execution.toString());
//  }
//
//  private void cleanDownloadedFiles() {
//    log.info("Cleaning downloaded csv files directory..");
//    fileService.clearDir();
//  }
//
//  private boolean hasJobParameterExecutionBatch(JobParameters jobParameters) {
//    Map<String, JobParameter<?>> parameters = jobParameters.getParameters();
//    return parameters.containsKey(
//        JobDescriptionConstants.EXECUTION_TYPE_PARAMETER) && JobDescriptionConstants.EXECUTION_BATCH_PARAMETER.equals(
//        parameters.get(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER).getValue());
//  }
//
//  private void clearImportedProcessedItem(JobExecution lastJobExecution) {
//    StepExecution stepExecution = lastJobExecution.getStepExecutions().stream().findFirst().get();
//    log.info("Clear processedItem from stepExecution: {}", stepExecution);
//    //TODO
//  }
//
//  private void updateLastJobExecutionStatus(JobExecution lastJobExecution) {
//    lastJobExecution.setStatus(BatchStatus.ABANDONED);
//    lastJobExecution.setExitStatus(new ExitStatus("RECOVERED"));
//    jobRepository.update(lastJobExecution);
//  }
//
//  private Job getJobToRecover(String jobName) {
//    if (JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME.equals(jobName)) {
//      return importServicePointCsvJob;
//    }
//    throw new IllegalStateException("No job found with name: " + jobName);
//  }
//
//  private JobParameters getJobParameters(JobExecution lastJobExecution) {
//    JobParameters lastJobExecutionJobParameters = lastJobExecution.getJobParameters();
//    Map<String, JobParameter<?>> parameters = lastJobExecutionJobParameters.getParameters();
//    JobParameter<?> fullPathFileName = parameters.get(JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER);
//
//    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
//        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis());
//
//    if (fullPathFileName != null) {
//      jobParametersBuilder.addJobParameter(JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER, fullPathFileName);
//    }
//    jobParametersBuilder.addJobParameters(lastJobExecutionJobParameters);
//    JobParameters jobParameters = jobParametersBuilder.toJobParameters();
//    log.info("Run job with parameters {}", jobParameters);
//    return jobParameters;
//  }
//}
