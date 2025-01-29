package ch.sbb.exportservice.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.observability.BatchMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailNotificationService {

  @Value("${mail.receiver.export-service}")
  private List<String> notificationAddresses;

  public MailNotification buildMailErrorNotification(String jobName, StepExecution stepExecution) {
    return MailNotification.builder()
        .to(notificationAddresses)
        .subject("Job [" + jobName + "] execution failed")
        .mailType(MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION)
        .templateProperties(buildErrorMailContent(jobName, stepExecution))
        .build();
  }

  private List<Map<String, Object>> buildErrorMailContent(String jobName, StepExecution stepExecution) {
    String stepExecutionInformation = getStepExecutionInformation(stepExecution);

    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("stepExecutionInformation", stepExecutionInformation);
    mailContentProperty.put("stepName", stepExecution.getStepName());
    mailContentProperty.put("exception", getException(stepExecution));
    mailContentProperty.put("cause", getCause(stepExecution));
    mailContentProperty.put("jobParameter", getParameters(stepExecution));
    mailContentProperty.put("correlationId", getCurrentSpan(stepExecution));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  String getException(StepExecution stepExecution) {
    StringBuilder errorBuilder = new StringBuilder();
    List<Throwable> failureExceptions = stepExecution.getFailureExceptions();
    failureExceptions.forEach(
        throwable -> errorBuilder.append(throwable.getLocalizedMessage()));
    return errorBuilder.toString();
  }

  String getCause(StepExecution stepExecution) {
    StringBuilder errorBuilder = new StringBuilder();
    List<Throwable> failureExceptions = stepExecution.getFailureExceptions();
    failureExceptions.forEach(
        throwable -> errorBuilder.append(throwable.getMessage()));
    return errorBuilder.toString();
  }

  String getParameters(StepExecution stepExecution) {
    StringBuilder stringBuilder = new StringBuilder();
    JobParameters jobParameters = stepExecution.getJobParameters();
    stringBuilder.append(jobParameters.getParameters());
    return stringBuilder.toString();
  }

  String getCurrentSpan(StepExecution stepExecution) {
    return stepExecution.getExecutionContext().getString("traceId", "TraceId not found!");
  }

  private String getStepExecutionInformation(StepExecution stepExecution) {
    Duration stepExecutionDuration = BatchMetrics.calculateDuration(stepExecution.getStartTime(), stepExecution.getEndTime());
    return "Step [" + stepExecution.getStepName() + " with id " + stepExecution.getId() + "] executed in "
        + BatchMetrics.formatDuration(stepExecutionDuration);
  }

}
