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

  public MailNotification buildWorkflowStartedMailNotification(LineWorkflow lineWorkflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.LINE_START_WORKFLOW_NOTIFICATION)
        .subject(buildStartSubject(lineWorkflow))
        .to(List.of(workflowLineReceiver))
        .cc(List.of(lineWorkflow.getClient().getMail()))
        .templateProperties(buildMailProperties(lineWorkflow))
        .build();
  }

  public MailNotification buildWorkflowApprovedMailNotification(LineWorkflow lineWorkflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.LINE_APPROVED_WORKFLOW_NOTIFICATION)
        .subject(buildApprovedSubject(lineWorkflow))
        .to(List.of(lineWorkflow.getClient().getMail()))
        .cc(List.of(atlasBusiness))
        .templateProperties(buildMailProperties(lineWorkflow))
        .build();
  }

  public MailNotification buildWorkflowCompletedMailNotification(LineWorkflow lineWorkflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.WORKFLOW_NOTIFICATION)
        .subject(buildSubject(lineWorkflow))
        .to(List.of(lineWorkflow.getClient().getMail()))
        .cc(List.of(atlasBusiness))
        .templateProperties(buildMailProperties(lineWorkflow))
        .build();
  }

  private String buildStartSubject(LineWorkflow lineWorkflow) {
    return "Antrag prüfen zu / vérifier la demande de  / controllare la richiesta per: " + lineWorkflow.getSwissId();
  }

  private String buildApprovedSubject(LineWorkflow lineWorkflow) {
    return "Antrag genehmigt / demande approuvée / richiesta approvata: " + lineWorkflow.getSwissId();
  }

  private String buildSubject(LineWorkflow lineWorkflow) {
    return "Antrag zu " + lineWorkflow.getSwissId() + " " + getWorkflowDescription(lineWorkflow) + " " + buildTranslatedStatus(
        lineWorkflow);
  }

  //todo: use this to generate subject
  private String buildTranslatedStatus(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED -> "prüfen";
      case APPROVED -> "genehmigt";
      case REJECTED -> "zurückgewiesen";
      default -> throw new IllegalArgumentException();
    };
  }

  private List<Map<String, Object>> buildMailProperties(LineWorkflow lineWorkflow) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", "Antrag für eine neue/geänderte Linie " + buildTranslatedStatus(lineWorkflow));
    mailContentProperty.put("teaser", getTeaser(lineWorkflow));
    mailContentProperty.put("swissId", lineWorkflow.getSwissId());
    mailContentProperty.put("description", getWorkflowDescription(lineWorkflow));
    mailContentProperty.put("checkComment", StringUtils.trimToNull(lineWorkflow.getCheckComment()));
    mailContentProperty.put("url", getUrl(lineWorkflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getTeaser(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED ->
          "Es wurde eine neue Linie bzw. eine Änderung an einer bestehenden Linie erfasst welche eine Freigabe erfordert.";
      case APPROVED, REJECTED ->
          "Der von Ihnen gestellte Antrag für die " + lineWorkflow.getSwissId() + " " + getWorkflowDescription(
              lineWorkflow) + " wurde überprüft und " + buildTranslatedStatus(lineWorkflow) + ".";
      default -> throw new IllegalArgumentException();
    };
  }

  private String getUrl(LineWorkflow lineWorkflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + LINE_URL + lineWorkflow.getSwissId() + "?id="
        + lineWorkflow.getBusinessObjectId();
  }

  private String getWorkflowDescription(LineWorkflow lineWorkflow) {
    if (StringUtils.isBlank(lineWorkflow.getDescription())) {
      return "(Keine Linienbezeichnung)";
    }
    return lineWorkflow.getDescription();
  }

}
