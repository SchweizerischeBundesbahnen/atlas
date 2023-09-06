package ch.sbb.exportservice.recovery;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import jakarta.validation.constraints.NotNull;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class RecoveryJobsRunner implements ApplicationListener<ApplicationReadyEvent> {

  private static final List<String> EXPORT_SERVICE_POINT_JOBS_NAME = List.of(
      JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME, JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME
  );
  private static final List<String> TRAFFIC_POINT_ELEMENT_JOBS_NAME = List.of(
      JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME,
      JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME
  );
  private static final List<String> EXPORT_LOADING_POINT_JOBS_NAME = List.of(
      JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME, JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME
  );
  private static final int CSV_OR_JSON_EXPORTS_JOB_EXECUTION_SIZE = 6;

  private final JobExplorer jobExplorer;
  private final FileService fileService;
  private final JobRepository jobRepository;

  private final ExportServicePointJobService exportServicePointJobService;
  private final ExportTrafficPointElementJobService exportTrafficPointElementJobService;
  private final ExportLoadingPointJobService exportLoadingPointJobService;

  @Override
  @Async
  public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
    log.info("Start checking jobs to recover...");
    cleanDownloadedFiles();
    checkExportServicePointJobToRecover();
    checkExportTrafficPointJobToRecover();
    checkExportLoadingPointJobToRecover();
  }

  private boolean checkIfHasJobsToRecover(List<String> exportJobsName) {
    log.info("Start checking {} jobs to recover...", exportJobsName);
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
    lastJobExecution.setExitStatus(new ExitStatus("RECOVERED"));
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
      List<JobInstance> lastSixJobInstances = jobExplorer.getJobInstances(job,
          0, CSV_OR_JSON_EXPORTS_JOB_EXECUTION_SIZE);
      for (JobInstance jobInstance : lastSixJobInstances) {
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
    if (checkIfHasJobsToRecover(EXPORT_SERVICE_POINT_JOBS_NAME)) {
      log.info("Rerunning {} export jobs...", EXPORT_SERVICE_POINT_JOBS_NAME);
      exportServicePointJobService.startExportJobs();
      log.info("All export jobs successfully recovered!");
    } else {
      log.info("No job found to recover.");
    }
  }

  private void checkExportTrafficPointJobToRecover() {
    if (checkIfHasJobsToRecover(TRAFFIC_POINT_ELEMENT_JOBS_NAME)) {
      log.info("Rerunning {} export jobs...", TRAFFIC_POINT_ELEMENT_JOBS_NAME);
      exportTrafficPointElementJobService.startExportJobs();
      log.info("All export jobs successfully recovered!");
    } else {
      log.info("No job found to recover.");
    }
  }

  private void checkExportLoadingPointJobToRecover() {
    if (checkIfHasJobsToRecover(EXPORT_LOADING_POINT_JOBS_NAME)) {
      log.info("Rerunning {} export jobs...", EXPORT_LOADING_POINT_JOBS_NAME);
      exportLoadingPointJobService.startExportJobs();
      log.info("All export jobs successfully recovered!");
    } else {
      log.info("No job found to recover.");
    }
  }

}
