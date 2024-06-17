package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.helper.AtlasFrontendBaseUrl;
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
public class StopPointWorkflowBuilderNotificationService {

  private static final String WORKFLOW_URL = "service-point-directory/workflows/";
  static final String START_WORKFLOW_SUBJECT = "Stationsnamen neue Anhörung / Nouvelle audition portant sur un nom de "
      + "station / Nome della stazione nuova audizione";
  static final String REJECT_WORKFLOW_SUBJECT = "Stationsname zurückgewiesen / Nom de station rejeté / Nome della "
      + "stazione respinto";

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  @Value("${mail.workflow.stop-point.from}")
  private String from;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      AtlasApiConstants.DATE_FORMAT_PATTERN_CH);

  public MailNotification buildWorkflowStartedExaminantMail(StopPointWorkflow stopPointWorkflow) {
    List<String> examinantMails = stopPointWorkflow.getExaminants().stream().map(Person::getMail).toList();
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION)
        .subject(START_WORKFLOW_SUBJECT)
        .to(examinantMails)
        .templateProperties(buildMailProperties(stopPointWorkflow, START_WORKFLOW_SUBJECT))
        .build();
  }

  public MailNotification buildWorkflowStartedCCMail(StopPointWorkflow stopPointWorkflow) {
    List<String> ccMails = stopPointWorkflow.getCcEmails() != null ? stopPointWorkflow.getCcEmails() : new ArrayList<>();
    ccMails.add(stopPointWorkflow.getApplicantMail());
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION)
        .subject(START_WORKFLOW_SUBJECT)
        .to(ccMails)
        .templateProperties(buildMailProperties(stopPointWorkflow, START_WORKFLOW_SUBJECT))
        .build();
  }

  public MailNotification buildWorkflowRejectMail(StopPointWorkflow stopPointWorkflow) {
    List<String> ccMails = stopPointWorkflow.getCcEmails();
    ccMails.addAll(stopPointWorkflow.getExaminants().stream().map(Person::getMail).toList());
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(REJECT_WORKFLOW_SUBJECT)
        .to(List.of(stopPointWorkflow.getApplicantMail()))
        .cc(ccMails)
        .templateProperties(buildMailProperties(stopPointWorkflow, REJECT_WORKFLOW_SUBJECT))
        .build();
  }

  List<Map<String, Object>> buildMailProperties(StopPointWorkflow stopPointWorkflow, String title) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", title);
    mailContentProperty.put("designationOfficial", stopPointWorkflow.getDesignationOfficial());
    mailContentProperty.put("sloid", stopPointWorkflow.getSloid());
    mailContentProperty.put("comment", stopPointWorkflow.getWorkflowComment());
    mailContentProperty.put("endDate", DATE_FORMATTER.format(stopPointWorkflow.getEndDate()));
    mailContentProperty.put("url", getUrl(stopPointWorkflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getUrl(StopPointWorkflow stopPointWorkflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + WORKFLOW_URL + stopPointWorkflow.getId();
  }

}
