package ch.sbb.importservice.module.geo.service;

import static ch.sbb.importservice.module.geo.job.ServicePointGeoLocationUpdateConfig.GEO_LOCATION_VERSIONS_KEY;

import ch.sbb.atlas.imports.ItemProcessResponseStatus;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.importservice.module.geo.entity.GeoUpdateProcessItem;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.observability.BatchMetrics;
import org.springframework.batch.core.step.StepExecution;
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
      List<GeoUpdateProcessItem> geoUpdateProcessItems,
      StepExecution stepExecution) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution successfully")
        .mailType(MailType.UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION)
        .templateProperties(buildSuccessMailContent(jobName, geoUpdateProcessItems, stepExecution))
        .build();
  }

  private List<Map<String, Object>> buildSuccessMailContent(String jobName,
      List<GeoUpdateProcessItem> geoUpdateProcessItems,
      StepExecution stepExecution) {
    String stepExecutionInformation = getJobInformation(stepExecution.getJobExecution());

    String geoLocationVersionsProcessed = String.valueOf(stepExecution.getExecutionContext().get(GEO_LOCATION_VERSIONS_KEY));
    log.info("GeoLocation Versions Processed: {}", geoLocationVersionsProcessed);

    List<GeoUpdateProcessItem> successUpdatedGeoItems = filterByStatus(geoUpdateProcessItems,
        ItemProcessResponseStatus.SUCCESS);
    List<GeoUpdateProcessItem> failedUpdatedGeoItems = filterByStatus(geoUpdateProcessItems,
        ItemProcessResponseStatus.FAILED);

    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("stepExecutionInformation", stepExecutionInformation);
    mailContentProperty.put("correlationId", getCurrentSpan(stepExecution));
    mailContentProperty.put("geoLocationVersionsToProcess", geoLocationVersionsProcessed);
    mailContentProperty.put("geoUpdateProcessItemsSize", geoUpdateProcessItems.size());
    mailContentProperty.put("successUpdatedGeoSize", successUpdatedGeoItems.size());
    mailContentProperty.put("failedUpdatedGeoItemsSize", failedUpdatedGeoItems.size());
    mailContentProperty.put("successUpdatedGeoItems", getUpdatedGeoItems(successUpdatedGeoItems));
    mailContentProperty.put("failedUpdatedGeoItems", getUpdatedGeoItems(failedUpdatedGeoItems));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private List<Map<String, Object>> getUpdatedGeoItems(List<GeoUpdateProcessItem> geoUpdateProcessItems) {
    return geoUpdateProcessItems.stream().map(item -> {
      Map<String, Object> object = new HashMap<>();
      object.put("processedItem", item.getSloid());
      object.put("processedItemId", item.getServicePointId());
      object.put("processedItemStatus", item.getResponseStatus());
      object.put("processedItemMessage", item.getResponseMessage());
      return object;
    }).toList();
  }

  private List<Map<String, Object>> buildErrorMailContent(String jobName, StepExecution stepExecution) {
    String stepExecutionInformation = getJobInformation(stepExecution.getJobExecution());

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
    stringBuilder.append(jobParameters);
    return stringBuilder.toString();
  }

  String getCurrentSpan(StepExecution stepExecution) {
    return stepExecution.getExecutionContext().getString("traceId");
  }

  private List<GeoUpdateProcessItem> filterByStatus(List<GeoUpdateProcessItem> allProcessedItem,
      ItemProcessResponseStatus status) {
    return allProcessedItem.stream()
        .filter(itemResponseStatus -> status.equals(itemResponseStatus.getResponseStatus())).toList();
  }

  private String getJobInformation(JobExecution jobExecution) {
    Duration jobExecutionDuration = BatchMetrics.calculateDuration(jobExecution.getStartTime(), jobExecution.getEndTime());
    return "Job [" + jobExecution.getJobInstance().getJobName() + " with id " + jobExecution.getId() + "] executed in "
        + BatchMetrics.formatDuration(jobExecutionDuration);
  }

}
