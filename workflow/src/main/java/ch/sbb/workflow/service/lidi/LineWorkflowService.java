package ch.sbb.workflow.service.lidi;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.helper.AtlasFrontendBaseUrl;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .to(List.of(workflow.getClient().getMail()))
                .cc(List.of(atlasBusiness))
                .templateProperties(buildMailProperties(workflow))
                .build();
    }

    private String buildSubject(Workflow workflow) {
        return "Antrag zu " + workflow.getSwissId() + " " + getWorkflowDescription(workflow) + " " + buildTranslatedStatus(workflow);
    }

    private String buildTranslatedStatus(Workflow workflow) {
        return switch (workflow.getStatus()) {
            case STARTED -> "prüfen";
            case APPROVED -> "genehmigt";
            case REJECTED -> "zurückgewiesen";
            default -> throw new IllegalArgumentException();
        };
    }

    private List<Map<String, Object>> buildMailProperties(Workflow workflow) {
        List<Map<String, Object>> mailProperties = new ArrayList<>();
        Map<String, Object> mailContentProperty = new HashMap<>();
        mailContentProperty.put("title", "Antrag für eine neue/geänderte Linie " + buildTranslatedStatus(workflow));
        mailContentProperty.put("teaser", getTeaser(workflow));
        mailContentProperty.put("swissId", workflow.getSwissId());
        mailContentProperty.put("description", getWorkflowDescription(workflow));
        mailContentProperty.put("checkComment", StringUtils.trimToNull(workflow.getCheckComment()));
        mailContentProperty.put("url", getUrl(workflow));
        mailProperties.add(mailContentProperty);
        return mailProperties;
    }

    private String getTeaser(Workflow workflow) {
        return switch (workflow.getStatus()) {
            case STARTED ->
                    "Es wurde eine neue Linie bzw. eine Änderung an einer bestehenden Linie erfasst welche eine Freigabe erfordert.";
            case APPROVED, REJECTED ->
                    "Der von Ihnen gestellte Antrag für die " + workflow.getSwissId() + " " + getWorkflowDescription(workflow) + " wurde überprüft und " + buildTranslatedStatus(workflow) + ".";
            default -> throw new IllegalArgumentException();
        };
    }

    private String getUrl(Workflow workflow) {
        return AtlasFrontendBaseUrl.getUrl(activeProfile) + LINE_URL + workflow.getSwissId() + "?id=" + workflow.getBusinessObjectId();
    }

    private String getWorkflowDescription(Workflow workflow){
        if (StringUtils.isBlank(workflow.getDescription())) {
            return "(Keine Linienbezeichnung)";
        }
        return workflow.getDescription();
    }

}
