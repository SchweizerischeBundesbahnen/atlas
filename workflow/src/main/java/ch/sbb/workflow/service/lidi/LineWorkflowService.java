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

  @Value("${mail.workflow.atlas.business}")
  private String atlasBusiness;

  @Value("${mail.workflow.line.mail-html-title}")
  private String mailHtmlTitle;

  public MailNotification buildWorkflowStartedMailNotification(Workflow workflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.WORKFLOW_NOTIFICATION)
        .subject(buildSubject(workflow))
        .to(List.of(workflowLineReceiver))
        .cc(List.of(workflow.getClient().getMail()))
        .templateProperties(buildMailProperties(workflow))
        .build();
  }

  public MailNotification buildWorkflowCompletedMailNotification(Workflow workflow) {
    return MailNotification.builder()
            .from(from)
            .mailType(MailType.WORKFLOW_NOTIFICATION)
            .subject(buildSubject(workflow))
            .to(List.of(workflow.getClient().getMail(), workflow.getExaminant().getMail()))
            .cc(List.of(atlasBusiness))
            .templateProperties(buildMailProperties(workflow))
            .build();
  }

  private String buildSubject(Workflow workflow) {
    String subject =  "Antrag zu " + workflow.getSwissId() + " " + workflow.getDescription();
    switch (workflow.getStatus()) {
      case STARTED -> subject+=" prüfen";
      case APPROVED -> subject+=" genehmigt";
      case REJECTED -> subject+=" zurückgewiesen";
    }
    return subject;
  }

  private List<Map<String, Object>> buildMailProperties(Workflow workflow) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", mailHtmlTitle);
    mailContentProperty.put("teaser", getTeaser(workflow));
    mailContentProperty.put("swissId", workflow.getSwissId());
    mailContentProperty.put("description", workflow.getDescription());
    mailContentProperty.put("checkComment", workflow.getCheckComment());
    mailContentProperty.put("url", getUrl(workflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getTeaser(Workflow workflow) {
    return switch (workflow.getStatus()) {
      case STARTED ->
              "Es wurde eine neue Linie bzw. eine Änderung an einer bestehenden Linie erfasst welche eine Freigabe erfordert.";
      case APPROVED ->
              "Der von Ihnen gestellte Antrag für die " + workflow.getSwissId() + " " + workflow.getDescription() + " wurde überprüft und genehmigt.";
      case REJECTED ->
              "Der von Ihnen gestellte Antrag für die " + workflow.getSwissId() + " " + workflow.getDescription() + " wurde überprüft und zurückgewiesen.";
      default -> throw new IllegalArgumentException();
    };
  }

  private String getUrl(Workflow workflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + LINE_URL + workflow.getSwissId();
  }

}
