package ch.sbb.importservice.service.mail;

import static ch.sbb.importservice.service.geo.ServicePointUpdateGeoLocationService.GEO_LOCATION_VERSIONS_KEY;

import ch.sbb.atlas.imports.ItemImportResponseStatus;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.importservice.entity.GeoUpdateImportProcessItem;
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
public class GeoLocationMailNotificationService {

  @Value("${mail.receiver.import-service-point}")
  private List<String> schedulingNotificationAddresses;

  public MailNotification buildMailErrorNotification(String jobName, StepExecution stepExecution) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution failed")
        .mailType(MailType.UPDATE_GEOLOCATION_ERROR_NOTIFICATION)
        .templateProperties(buildErrorMailContent(jobName, stepExecution))
        .build();
  }

  public MailNotification buildMailSuccessNotification(String jobName,
      List<GeoUpdateImportProcessItem> geoUpdateImportProcessItems,
      StepExecution stepExecution) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution successfully")
        .mailType(MailType.UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION)
        .templateProperties(buildSuccessMailContent(jobName, geoUpdateImportProcessItems, stepExecution))
        .build();
  }

  private List<Map<String, Object>> buildSuccessMailContent(String jobName,
      List<GeoUpdateImportProcessItem> geoUpdateImportProcessItems,
      StepExecution stepExecution) {
    String stepExecutionInformation = getStepExecutionInformation(stepExecution);

    String geoLocationVersionsProcessed = String.valueOf(stepExecution.getExecutionContext().get(GEO_LOCATION_VERSIONS_KEY));
    log.info("GeoLocation Versions Processed: {}", geoLocationVersionsProcessed);

    List<GeoUpdateImportProcessItem> successImportedItems = filterByStatus(geoUpdateImportProcessItems,
        ItemImportResponseStatus.SUCCESS);
    List<GeoUpdateImportProcessItem> failedImportedItems = filterByStatus(geoUpdateImportProcessItems,
        ItemImportResponseStatus.FAILED);

    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("stepExecutionInformation", stepExecutionInformation);
    mailContentProperty.put("correlationId", getCurrentSpan(stepExecution));
    mailContentProperty.put("geoLocationVersionsToProcess", geoLocationVersionsProcessed);
    mailContentProperty.put("importProcessItemsSize", geoUpdateImportProcessItems.size());
    mailContentProperty.put("successImportedItemsSize", successImportedItems.size());
    mailContentProperty.put("failedImportedItemsSize", failedImportedItems.size());
    mailContentProperty.put("successImportedItems", getImportProcessItem(successImportedItems));
    mailContentProperty.put("failedImportedItems", getImportProcessItem(failedImportedItems));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private List<Map<String, Object>> getImportProcessItem(List<GeoUpdateImportProcessItem> geoUpdateImportProcessItems) {
    return geoUpdateImportProcessItems.stream().map(item -> {
      Map<String, Object> object = new HashMap<>();
      object.put("processedItem", item.getSloid());
      object.put("processedItemId", item.getServicePointId());
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

  private List<GeoUpdateImportProcessItem> filterByStatus(List<GeoUpdateImportProcessItem> allImportProcessedItem,
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
