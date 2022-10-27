package ch.sbb.workflow.service.lidi;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.helper.AtlasFrontendBaseUrl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LineWorkflowService {

  private static final String LINE_URL = "line-directory/lines/";

  @Value("${spring.profiles.active:local}")
  private String activeProfile;
  @Value("${mail.workflow.line.receiver}")
  private String workflowLineReceiver;

  @Value("${mail.workflow.line.from}")
  private String from;

  @Value("${mail.workflow.line.mail-html-title}")
  private String mailHtmlTitle;

  public MailNotification buildMailNotification(Workflow workflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.WORKFLOW_NOTIFICATION)
        .subject(buildSubject(workflow))
        .to(List.of(workflowLineReceiver))
        .cc(List.of(workflow.getClient().getMail()))
        .templateProperties(buildMailPropeties(workflow))
        .build();
  }

  private String buildSubject(Workflow workflow) {
    return "Betreff: Antrag zu " + workflow.getSwissId() + " " + workflow.getDescription() + " prüfen";
  }

  private List<Map<String, Object>> buildMailPropeties(Workflow workflow) {

    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", mailHtmlTitle);
    mailContentProperty.put("swissId", workflow.getSwissId());
    mailContentProperty.put("description", workflow.getDescription());
    mailContentProperty.put("url", getUrl(workflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getUrl(Workflow workflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + LINE_URL + workflow.getSwissId();
  }

}
