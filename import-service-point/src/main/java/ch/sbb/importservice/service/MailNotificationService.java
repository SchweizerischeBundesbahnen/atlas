package ch.sbb.importservice.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.importservice.entitiy.ImportProcessItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailNotificationService {

  private final Tracer tracer;

  @Value("${mail.receiver.import-service-point}")
  private List<String> schedulingNotificationAddresses;

  public MailNotification buildMailErrorNotification(String jobName, StepExecution stepExecution) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution failed")
        .mailType(MailType.IMPORT_SERVICE_POINT_ERROR_NOTIFICATION)
        .templateProperties(buildErrorMailContent(jobName, stepExecution))
        .build();
  }

  public MailNotification buildMailSuccessNotification(String jobName, List<ImportProcessItem> importProcessItems) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution successfully")
        .mailType(MailType.IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION)
        .templateProperties(buildSuccessMailContent(jobName, importProcessItems))
        .build();
  }

  private List<Map<String, Object>> buildSuccessMailContent(String jobName, List<ImportProcessItem> importProcessItems) {

    List<Map<String, Object>> importProcessItem = getImportProcessItem(importProcessItems);
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("correlationId", getCurrentSpan());
    mailContentProperty.put("importProcessItems", importProcessItem);
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private List<Map<String, Object>> getImportProcessItem(List<ImportProcessItem> importProcessItems) {
    List<Map<String, Object>> importProcessItem = importProcessItems.stream().map(item -> {
      Map<String, Object> object = new HashMap<>();
      object.put("importProcessItem", item.getServicePointNumber());
      return object;
    }).toList();
    return importProcessItem;
  }

  private List<Map<String, Object>> buildErrorMailContent(String jobName, StepExecution stepExecution) {

    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("exception", getException(stepExecution));
    mailContentProperty.put("jobParameters", getParameters(stepExecution));
    mailContentProperty.put("correlationId", getCurrentSpan());
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  String getException(StepExecution stepExecution) {
    StringBuilder errorBuilder = new StringBuilder();
    List<Throwable> failureExceptions = stepExecution.getFailureExceptions();
    failureExceptions.forEach(
        throwable -> errorBuilder.append("Exception: ").append(throwable.getLocalizedMessage()).append("\n")
            .append("Cause: ").append(throwable.getCause().getLocalizedMessage()).append("\n"));
    return errorBuilder.toString();
  }

  String getParameters(StepExecution stepExecution) {
    StringBuilder stringBuilder = new StringBuilder();
    JobParameters jobParameters = stepExecution.getJobParameters();
    jobParameters.getParameters().keySet().forEach(s -> stringBuilder.append("JobParameter: ").append(s).append("\n"));
    return stringBuilder.toString();
  }

  String getCurrentSpan() {
    if (tracer.currentSpan() != null) {
      TraceContext context = Objects.requireNonNull(tracer.currentSpan()).context();
      return context.traceId();
    }
    throw new IllegalStateException("No Tracer found!");
  }

}
