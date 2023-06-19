package ch.sbb.exportservice.recovery;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.service.ExportJobService;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
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
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RecoveryJobsRunner implements ApplicationListener<ApplicationReadyEvent> {

  private static final List<String> JOBS = List.of(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME,
      JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME);
  private static final int ALL_EXPORTS_JOB_EXECUTION_SIZE = 12;
  private static final int CSV_OR_JSON_EXPORTS_JOB_EXECUTION_SIZE = 6;
  private final JobExplorer jobExplorer;
  private final FileService fileService;

  private final JobRepository jobRepository;
  private final ExportJobService exportJobService;

  private boolean checkIfHasJobsToRecover() {
    if (!isTheFirstJobsExecution()) {
      List<JobExecution> todayExecutedJobs = getTodayJobExecutions();
      for (JobExecution jobExecution : todayExecutedJobs) {
        if (jobExecution != null && jobExecution.getStatus().isRunning()) {
          updateLastJobExecutionStatus(jobExecution);
          log.info("Found job to recovery: {}", jobExecution);
          return true;
        }
      }
      if (todayExecutedJobs.size() < ALL_EXPORTS_JOB_EXECUTION_SIZE) {
        log.info("Not all export jobs were executed..");
        return true;
      }
    }
    return false;
  }

  private void updateLastJobExecutionStatus(JobExecution lastJobExecution) {
    lastJobExecution.setStatus(BatchStatus.ABANDONED);
    lastJobExecution.setExitStatus(new ExitStatus("RECOVERED"));
    jobRepository.update(lastJobExecution);
  }

  private boolean isTheFirstJobsExecution() {
    int totalJobExecutionCount = 0;
    for (String job : JOBS) {
      try {
        totalJobExecutionCount += jobExplorer.getJobInstanceCount(job);
      } catch (NoSuchJobException e) {
        throw new RuntimeException(e);
      }
    }
    return totalJobExecutionCount <= 0;
  }

  private List<JobExecution> getTodayJobExecutions() {
    Set<JobExecution> executedJobs = new HashSet<>();
    for (String job : JOBS) {
      List<JobInstance> jobInstances = jobExplorer.getJobInstances(job,
          0, CSV_OR_JSON_EXPORTS_JOB_EXECUTION_SIZE);
      for (JobInstance jobInstance : jobInstances) {
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

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("Checking jobs to recover...");
    cleanDownloadedFiles();
    if (checkIfHasJobsToRecover()) {
      log.info("Rerunning all export jobs...");
      exportJobService.startExportJobs();
      log.info("All export jobs successfully recovered!");
    } else {
      log.info("No job found to recover.");
    }
  }
}
