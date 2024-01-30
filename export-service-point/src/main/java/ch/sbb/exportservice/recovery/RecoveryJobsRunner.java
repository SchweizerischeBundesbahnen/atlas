package ch.sbb.exportservice.recovery;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.exportservice.service.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.*;

@Component
@AllArgsConstructor
@Slf4j
public class RecoveryJobsRunner implements ApplicationListener<ApplicationReadyEvent> {

  private static final List<String> EXPORT_SERVICE_POINT_JOBS_NAME = List.of(EXPORT_SERVICE_POINT_CSV_JOB_NAME,
      EXPORT_SERVICE_POINT_JSON_JOB_NAME);
  private static final List<String> TRAFFIC_POINT_ELEMENT_JOBS_NAME = List.of(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME,
      EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME);
  private static final List<String> EXPORT_LOADING_POINT_JOBS_NAME = List.of(EXPORT_LOADING_POINT_CSV_JOB_NAME,
      EXPORT_LOADING_POINT_JSON_JOB_NAME);
  private static final List<String> EXPORT_STOP_POINT_JOBS_NAME = List.of(EXPORT_STOP_POINT_CSV_JOB_NAME,
      EXPORT_STOP_POINT_JSON_JOB_NAME);
  private static final List<String> EXPORT_PLATFORM_JOBS_NAME = List.of(EXPORT_PLATFORM_CSV_JOB_NAME,
          EXPORT_PLATFORM_JSON_JOB_NAME);
  private static final List<String> EXPORT_REFERENCE_POINT_JOB_NAME = List.of(EXPORT_REFERENCE_POINT_CSV_JOB_NAME,
      EXPORT_REFERENCE_POINT_JSON_JOB_NAME);
  static final int TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE = 8;
  public static final String ATLAS_BATCH_STATUS_RECOVERED = "RECOVERED";

  private final JobExplorer jobExplorer;
  private final FileService fileService;
  private final JobRepository jobRepository;

  private final ExportServicePointJobService exportServicePointJobService;
  private final ExportTrafficPointElementJobService exportTrafficPointElementJobService;
  private final ExportLoadingPointJobService exportLoadingPointJobService;
  private final ExportStopPointJobService exportStopPointJobService;
  private final ExportPlatformJobService exportPlatformJobService;
  private final ExportReferencePointJobService exportReferencePointJobService;

  @Override
  @Async
  public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
    log.info("Start checking jobs to recover...");
    cleanDownloadedFiles();
    checkExportServicePointJobToRecover();
    checkExportTrafficPointJobToRecover();
    checkExportLoadingPointJobToRecover();
    checkExportStopPointJobToRecover();
    checkExportPlatformJobToRecover();
    checkExportReferencePointJobToRecover();
  }

  private boolean checkIfHasJobsToRecover(List<String> exportJobsName) {
    log.info("Start checking {} jobs to recover...", exportJobsName);
    //We want to avoid that the jobs are started when we start the Export-Service with an Empty Data-DB (e.g. local)
    if (!isTheFirstJobsExecution(exportJobsName)) {
      List<JobExecution> todayExecutedJobs = getTodayJobExecutions(exportJobsName);
      for (JobExecution jobExecution : todayExecutedJobs) {
        if (jobExecution != null && jobExecution.getStatus().isRunning()) {
          updateLastJobExecutionStatus(jobExecution);
          log.info("Found job to recovery: {}", jobExecution);
          return true;
        }
      }
    }
    return false;
  }

  private void updateLastJobExecutionStatus(JobExecution lastJobExecution) {
    lastJobExecution.setStatus(BatchStatus.ABANDONED);
    lastJobExecution.setExitStatus(new ExitStatus(ATLAS_BATCH_STATUS_RECOVERED));
    jobRepository.update(lastJobExecution);
  }

  private boolean isTheFirstJobsExecution(List<String> exportJobsName) {
    int totalJobExecutionCount = 0;
    for (String job : exportJobsName) {
      try {
        totalJobExecutionCount += (int) jobExplorer.getJobInstanceCount(job);
      } catch (NoSuchJobException e) {
        throw new JobExecutionException(job, e);
      }
    }
    return totalJobExecutionCount <= 0;
  }

  private List<JobExecution> getTodayJobExecutions(List<String> exportJobsName) {
    Set<JobExecution> executedJobs = new HashSet<>();
    for (String job : exportJobsName) {
      List<JobInstance> lastExecutedJobInstances = jobExplorer.getJobInstances(job,
          0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE);
      for (JobInstance jobInstance : lastExecutedJobInstances) {
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
        executedJobs.addAll(jobExecutions);
      }
    }
    return executedJobs.stream()
        .filter(jobExecution -> LocalDate.now().isEqual(jobExecution.getCreateTime().toLocalDate())).toList();
  }

  private void cleanDownloadedFiles() {
    log.info("Cleaning downloaded csv files directory..");
    fileService.clearDir();
  }

  private void checkExportServicePointJobToRecover() {
    checkJobToRecover(exportServicePointJobService, EXPORT_SERVICE_POINT_JOBS_NAME);
  }

  private void checkExportTrafficPointJobToRecover() {
    checkJobToRecover(exportTrafficPointElementJobService, TRAFFIC_POINT_ELEMENT_JOBS_NAME);
  }

  private void checkExportLoadingPointJobToRecover() {
    checkJobToRecover(exportLoadingPointJobService, EXPORT_LOADING_POINT_JOBS_NAME);
  }

  private void checkExportStopPointJobToRecover() {
    checkJobToRecover(exportStopPointJobService, EXPORT_STOP_POINT_JOBS_NAME);
  }

  private void checkExportPlatformJobToRecover() {
    checkJobToRecover(exportPlatformJobService, EXPORT_PLATFORM_JOBS_NAME);
  }

  private void checkExportReferencePointJobToRecover() {
    checkJobToRecover(exportReferencePointJobService, EXPORT_REFERENCE_POINT_JOB_NAME);
  }

  private void checkJobToRecover(BaseExportJobService jobService, List<String> jobsName) {
    if (checkIfHasJobsToRecover(jobsName)) {
      logRerunning(jobsName);
      jobService.startExportJobs();
      lodRecovered();
    } else {
      logNotFound();
    }
  }

  private static void logNotFound() {
    log.info("No job found to recover.");
  }

  private static void lodRecovered() {
    log.info("All export jobs successfully recovered!");
  }

  private static void logRerunning(List<String> jobs) {
    log.info("Rerunning {} export jobs...", jobs);
  }

}
