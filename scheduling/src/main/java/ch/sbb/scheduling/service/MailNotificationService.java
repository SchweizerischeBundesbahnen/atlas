package ch.sbb.scheduling.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailNotificationService {

  private final Tracer tracer;

  @Value("${mail.receiver.schduling}")
  private List<String> schedulingNotificationAddresses;

  public MailNotification buildMailNotification(String jobName, Throwable throwable) {
    return MailNotification.builder()
        .to(schedulingNotificationAddresses)
        .subject("Job [" + jobName + "] execution failed")
        .mailType(MailType.SCHEDULING_ERROR_NOTIFICATION)
        .templateProperties(buildMailContent(jobName, throwable))
        .build();
  }

  private List<Map<String, Object>> buildMailContent(String jobName, Throwable throwable) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("jobName", jobName);
    mailContentProperty.put("error", throwable.getLocalizedMessage());
    mailContentProperty.put("correlationId", Objects.requireNonNull(tracer.currentSpan()).context().traceId());
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

}
