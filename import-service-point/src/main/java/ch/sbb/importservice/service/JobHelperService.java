package ch.sbb.importservice.service;

import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class JobHelperService {

  public static final LocalDate MIN_LOCAL_DATE = LocalDate.of(1700, 1, 1);
  private final JobExplorer jobExplorer;
  
  @Getter
  @Value("${batch.config.service-point-directory.chuck-size}")
  private Integer servicePointDirectoryChunkSize;

  public boolean isDateMatchedBetweenTodayAndMatchingDate(LocalDate matchingDate, LocalDate lastEditionDate) {
    LocalDate today = LocalDate.now();
    if (today.isEqual(matchingDate)) {
      return lastEditionDate.isEqual(matchingDate);
    } else {
      //check lastEditionDate is between matchingDate and today
      return !lastEditionDate.isBefore(matchingDate) && !lastEditionDate.isAfter(today);
    }
  }

  public LocalDate getDateForImportFileToDownload(String jobName) {
    List<JobExecution> jobExecutions = getBatchJobExecutions(jobName);
    if (!jobExecutions.isEmpty()) {
      JobExecution lastSuccessfullyJobExecution = getLastSuccessfullyJobExecution(jobExecutions);
      if (lastSuccessfullyJobExecution != null) {
        return lastSuccessfullyJobExecution.getCreateTime().toLocalDate();
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

  private List<JobExecution> getBatchJobExecutions(String jobName) {
    List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(jobName, 0,
        Integer.MAX_VALUE);
    List<JobExecution> jobExecutions = new ArrayList<>();
    jobInstances.forEach(jobInstance -> {
      JobExecution lastJobExecution = jobExplorer.getLastJobExecution(jobInstance);
      if (lastJobExecution != null) {
        Map<String, JobParameter<?>> parameters = lastJobExecution.getJobParameters().getParameters();
        if (parameters.containsKey(EXECUTION_TYPE_PARAMETER) && EXECUTION_BATCH_PARAMETER.equals(
            parameters.get(EXECUTION_TYPE_PARAMETER).getValue())) {
          jobExecutions.add(lastJobExecution);
        }
      }
    });
    return jobExecutions;
  }

}
