package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointNotificationService {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      AtlasApiConstants.DATE_FORMAT_PATTERN_CH);

  private static final String WORKFLOW_URL = "service-point-directory/workflows/";

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  @Value("${mail.workflow.stop-point.from}")
  private String from;

  //TODO: create abstract class
  private final MailProducerService mailProducerService;
  private final TerminationExaminants terminationExaminants;

  public void sendStartTerminationNotificationToInfoPlus(TerminationStopPointWorkflow workflow) {
    MailNotification notification = buildStartTerminationNotificationMailForInfoPlus(workflow);
    mailProducerService.produceMailNotification(notification);
  }

  public void sendStartConfirmationTerminationNotificationToApplicantMail(TerminationStopPointWorkflow workflow) {
    //add mailTemplate2
  }

  private MailNotification buildStartTerminationNotificationMailForInfoPlus(TerminationStopPointWorkflow workflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT)
        .to(List.of(terminationExaminants.getInfoPlus()))
        .templateProperties(buildMailProperties(workflow, TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT))
        .build();
  }

  public List<Map<String, Object>> buildMailProperties(TerminationStopPointWorkflow workflow, String title) {
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
    //todo: implement logic
    return DATE_FORMATTER.format(workflow.getBoTerminationDate());
  }

}
