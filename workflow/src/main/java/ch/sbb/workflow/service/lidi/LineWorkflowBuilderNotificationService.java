package ch.sbb.workflow.service.lidi;

import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.entity.LineWorkflow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LineWorkflowBuilderNotificationService {

  private static final String LINE_URL = "line-directory/lines/";

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  @Value("${mail.workflow.line.receiver}")
  private String workflowLineReceiver;

  @Value("${mail.workflow.line.from}")
  private String from;

  @Value("${mail.workflow.atlas.business}")
  private String atlasBusiness;

  public MailNotification buildWorkflowMailNotification(LineWorkflow lineWorkflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(getMailType(lineWorkflow))
        .subject(buildSubject(lineWorkflow))
        .to(getTo(lineWorkflow))
        .cc(getCC(lineWorkflow))
        .templateProperties(buildMailProperties(lineWorkflow))
        .build();
  }

  List<String> getCC(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED -> List.of(lineWorkflow.getClient().getMail());
      case APPROVED, REJECTED -> List.of(atlasBusiness);
      default -> throw new IllegalArgumentException();
    };
  }

  List<String> getTo(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED -> List.of(workflowLineReceiver);
      case APPROVED, REJECTED -> List.of(lineWorkflow.getClient().getMail());
      default -> throw new IllegalArgumentException();
    };
  }

  MailType getMailType(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED -> MailType.LINE_STARTED_WORKFLOW_NOTIFICATION;
      case APPROVED -> MailType.LINE_APPROVED_WORKFLOW_NOTIFICATION;
      case REJECTED -> MailType.LINE_REJECTED_WORKFLOW_NOTIFICATION;
      default -> throw new IllegalArgumentException();
    };
  }

  private String buildSubject(LineWorkflow lineWorkflow) {
    return LineWorkflowSubject.getSubject(lineWorkflow) + lineWorkflow.getSwissId();
  }

  private List<Map<String, Object>> buildMailProperties(LineWorkflow lineWorkflow) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("swissId", lineWorkflow.getSwissId());
    mailContentProperty.put("number", lineWorkflow.getNumber());
    mailContentProperty.put("description", lineWorkflow.getDescription());
    mailContentProperty.put("checkComment", StringUtils.trimToNull(lineWorkflow.getCheckComment()));
    mailContentProperty.put("url", getUrl(lineWorkflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getUrl(LineWorkflow lineWorkflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + LINE_URL + lineWorkflow.getSwissId() + "?id="
        + lineWorkflow.getBusinessObjectId();
  }

}
