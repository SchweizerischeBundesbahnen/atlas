package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.mail.BaseNotificationService;
import ch.sbb.workflow.sepodi.termination.TerminationHelper;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointWorkflowBuilderNotificationService extends BaseNotificationService {

  private final TerminationExaminants terminationExaminants;

  public MailNotification buildStartTerminationNotificationMailForInfoPlus(TerminationStopPointWorkflow workflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT)
        .to(List.of(terminationExaminants.getInfoPlus()))
        .templateProperties(buildMailProperties(workflow, TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT))
        .build();
  }

  private List<Map<String, Object>> buildMailProperties(TerminationStopPointWorkflow workflow, String title) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", title);
    mailContentProperty.put("designationOfficial", workflow.getDesignationOfficial());
    mailContentProperty.put("sloid", workflow.getSloid());
    mailContentProperty.put("url", getUrl(workflow));
    mailContentProperty.put("terminationDate", calculateTerminationDate(workflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getUrl(TerminationStopPointWorkflow workflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + WORKFLOW_URL + workflow.getId();
  }

  private String calculateTerminationDate(TerminationStopPointWorkflow workflow) {
    return DATE_FORMATTER.format(TerminationHelper.getTerminationDate(workflow));
  }
}
