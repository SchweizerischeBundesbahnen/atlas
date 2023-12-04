package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.importservice.entity.ImportProcessItem;
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

  public MailNotification buildMailSuccessNotification(String jobName, List<ImportProcessItem> importProcessItems,
      StepExecution stepExecution) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution successfully")
        .mailType(MailType.IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION)
        .templateProperties(buildSuccessMailContent(jobName, importProcessItems, stepExecution))
        .build();
  }

  private List<Map<String, Object>> buildSuccessMailContent(String jobName, List<ImportProcessItem> importProcessItems,
      StepExecution stepExecution) {
    String stepExecutionInformation = getStepExecutionInformation(stepExecution);

    List<ImportProcessItem> successImportedItems = filterByStatus(importProcessItems, ItemImportResponseStatus.SUCCESS);
    List<ImportProcessItem> successWarningImportedItems = filterByStatus(importProcessItems, ItemImportResponseStatus.WARNING);
    List<ImportProcessItem> failedImportedItems = filterByStatus(importProcessItems, ItemImportResponseStatus.FAILED);

    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("stepExecutionInformation", stepExecutionInformation);
    mailContentProperty.put("correlationId", getCurrentSpan(stepExecution));
    mailContentProperty.put("importProcessItemsSize", importProcessItems.size());
    mailContentProperty.put("successImportedItemsSize", successImportedItems.size());
    mailContentProperty.put("successWarningImportedItemsSize", successWarningImportedItems.size());
    mailContentProperty.put("failedImportedItemsSize", failedImportedItems.size());
    if (failedImportedItems.size() < 1_000) {
      mailContentProperty.put("failedImportedItems", getImportProcessItem(failedImportedItems));
    } else {
      mailContentProperty.put("failedImportedItems", new HashMap<>());
    }

    if(successWarningImportedItems.size() < 1_000){
      mailContentProperty.put("successWarningImportedItems", getImportProcessItem(successWarningImportedItems));
    }
    else{
      mailContentProperty.put("successWarningImportedItems", new HashMap<>());
    }
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private List<Map<String, Object>> getImportProcessItem(List<ImportProcessItem> importProcessItems) {
    return importProcessItems.stream().map(item -> {
      Map<String, Object> object = new HashMap<>();
      object.put("processedItem", item.getItemNumber());
      object.put("processedItemStatus", item.getResponseStatus());
      object.put("processedItemMessage", item.getResponseMessage());
      return object;
    }).toList();
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
        throwable -> errorBuilder.append(throwable.getCause().getLocalizedMessage()));
    return errorBuilder.toString();
  }

  String getParameters(StepExecution stepExecution) {
    StringBuilder stringBuilder = new StringBuilder();
    JobParameters jobParameters = stepExecution.getJobParameters();
    stringBuilder.append(jobParameters.getParameters());
    return stringBuilder.toString();
  }

  String getCurrentSpan(StepExecution stepExecution) {
    return stepExecution.getExecutionContext().getString("traceId");
  }

  private List<ImportProcessItem> filterByStatus(List<ImportProcessItem> allImportProcessedItem,
      ItemImportResponseStatus status) {
    return allImportProcessedItem.stream()
        .filter(importProcessItem -> status.equals(importProcessItem.getResponseStatus())).toList();
  }

  private String getStepExecutionInformation(StepExecution stepExecution) {
    Duration stepExecutionDuration = BatchMetrics.calculateDuration(stepExecution.getStartTime(), stepExecution.getEndTime());
    return "Step [" + stepExecution.getStepName() + " with id " + stepExecution.getId() + "] executed in "
        + BatchMetrics.formatDuration(stepExecutionDuration);
  }

}
