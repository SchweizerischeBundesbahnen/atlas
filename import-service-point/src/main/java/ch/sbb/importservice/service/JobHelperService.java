package ch.sbb.importservice.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class JobHelperService {

  private static final LocalDate MIN_LOCAL_DATE = LocalDate.of(1700, 1, 1);
  private final JobExplorer jobExplorer;

  public boolean isDateMatchedBetweenTodayAndMatchingDate(LocalDate matchingDate, LocalDate lastEditionDate) {
    LocalDate today = LocalDate.now();
    if (today.isEqual(matchingDate)) {
      return lastEditionDate.isEqual(matchingDate);
    } else {
      //check  is between matchingDate and today
      return !lastEditionDate.isBefore(matchingDate) && !lastEditionDate.isAfter(today);
    }
  }

  public LocalDate getDateForImportFileToDownload(String jobName) {
    List<JobExecution> jobExecutions = getJobExecutions(jobName);
    if (!jobExecutions.isEmpty()) {
      JobExecution lastSuccessfullyJobExecution = getLastSuccessfullyJobExecution(jobExecutions);
      if (lastSuccessfullyJobExecution != null) {
        Date lastSuccessfullyJobExecutionCreateTime = lastSuccessfullyJobExecution.getCreateTime();
        return convertToLocalDateViaInstant(lastSuccessfullyJobExecutionCreateTime);
      }
    }
    //In this case there is no COMPLETED Job, so we need to import the whole File
    return MIN_LOCAL_DATE;
  }

  private JobExecution getLastSuccessfullyJobExecution(List<JobExecution> jobExecutions) {
    jobExecutions.sort(Comparator.comparing(JobExecution::getCreateTime).reversed());
    return jobExecutions.stream()
        .filter(jobExecution -> BatchStatus.COMPLETED == jobExecution.getStatus()).findFirst().orElse(null);
  }

  private List<JobExecution> getJobExecutions(String jobName) {
    List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(jobName, 0,
        Integer.MAX_VALUE);
    List<JobExecution> jobExecutions = new ArrayList<>();
    jobInstances.forEach(jobInstance -> {
      JobExecution lastJobExecution = jobExplorer.getLastJobExecution(jobInstance);
      jobExecutions.add(lastJobExecution);
    });
    return jobExecutions;
  }

  private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

}
