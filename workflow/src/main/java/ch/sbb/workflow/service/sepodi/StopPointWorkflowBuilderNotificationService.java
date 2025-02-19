package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  @Value("${mail.workflow.stop-point.from}")
  private String from;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      AtlasApiConstants.DATE_FORMAT_PATTERN_CH);

  public MailNotification buildWorkflowStartedExaminantMail(StopPointWorkflow stopPointWorkflow) {
    List<String> examinantMails = stopPointWorkflow.getExaminants().stream().map(Person::getMail).toList();
    return buildWorkflowStartedExaminantMail(stopPointWorkflow, examinantMails);
  }

  public MailNotification buildWorkflowStartedExaminantMail(StopPointWorkflow stopPointWorkflow, List<String> examinantMails) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION)
        .subject(WorkflowSubject.START_WORKFLOW_SUBJECT)
        .to(examinantMails)
        .templateProperties(buildMailProperties(stopPointWorkflow, WorkflowSubject.START_WORKFLOW_SUBJECT))
        .build();
  }

  public MailNotification buildWorkflowStartedCCMail(StopPointWorkflow stopPointWorkflow) {
    List<String> ccMails = new ArrayList<>();
    ccMails.add(stopPointWorkflow.getApplicantMail());
    ccMails.addAll(stopPointWorkflow.getCcEmails());
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION)
        .subject(WorkflowSubject.START_WORKFLOW_SUBJECT)
        .to(ccMails)
        .templateProperties(buildMailProperties(stopPointWorkflow, WorkflowSubject.START_WORKFLOW_SUBJECT))
        .build();
  }

  public MailNotification buildWorkflowRejectMail(StopPointWorkflow stopPointWorkflow, String rejectComment) {
    List<String> ccMails = new ArrayList<>();
    ccMails.addAll(stopPointWorkflow.getCcEmails());
    ccMails.addAll(stopPointWorkflow.getExaminants().stream().map(Person::getMail).toList());
    List<Map<String, Object>> templateProperties = buildMailProperties(stopPointWorkflow,
        WorkflowSubject.REJECT_WORKFLOW_SUBJECT);
    templateProperties.getFirst().put("comment", rejectComment);
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(WorkflowSubject.REJECT_WORKFLOW_SUBJECT)
        .to(List.of(stopPointWorkflow.getApplicantMail()))
        .cc(ccMails)
        .templateProperties(templateProperties)
        .build();
  }

  public MailNotification buildPinCodeMail(StopPointWorkflow stopPointWorkflow, String examinantMail, String pinCode) {
    List<Map<String, Object>> templateProperties = buildMailProperties(stopPointWorkflow, WorkflowSubject.PINCODE_SUBJECT);
    templateProperties.getFirst().put("pincode", pinCode);
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION)
        .subject(WorkflowSubject.PINCODE_SUBJECT)
        .to(Collections.singletonList(examinantMail))
        .templateProperties(templateProperties)
        .build();
  }

  public MailNotification buildWorkflowApprovedMail(StopPointWorkflow stopPointWorkflow) {
    List<String> recipients = new ArrayList<>();
    recipients.add(stopPointWorkflow.getApplicantMail());
    recipients.addAll(stopPointWorkflow.getExaminants().stream().map(Person::getMail).toList());
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(WorkflowSubject.APPROVED_WORKFLOW_SUBJECT)
        .to(recipients)
        .cc(stopPointWorkflow.getCcEmails())
        .templateProperties(buildMailProperties(stopPointWorkflow, WorkflowSubject.APPROVED_WORKFLOW_SUBJECT))
        .build();
  }

  public MailNotification buildWorkflowCanceledMail(StopPointWorkflow stopPointWorkflow, String cancelComment) {
    List<String> recipients = new ArrayList<>();
    recipients.add(stopPointWorkflow.getApplicantMail());
    recipients.addAll(stopPointWorkflow.getExaminants().stream().map(Person::getMail).toList());

    List<Map<String, Object>> templateProperties = buildMailProperties(stopPointWorkflow,
        WorkflowSubject.CANCEL_WORKFLOW_SUBJECT);
    templateProperties.getFirst().put("comment", cancelComment);
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(WorkflowSubject.CANCEL_WORKFLOW_SUBJECT)
        .to(recipients)
        .cc(stopPointWorkflow.getCcEmails())
        .templateProperties(templateProperties)
        .build();
  }

  public MailNotification buildWorkflowRestartedMail(StopPointWorkflow existingStopPointWorkflow,
      StopPointWorkflow newStopPointWorkflow) {
    List<String> examinantMails = newStopPointWorkflow.getExaminants().stream().map(Person::getMail).toList();
    List<Map<String, Object>> templateProperties = buildMailProperties(newStopPointWorkflow,
        WorkflowSubject.RESTART_WORKFLOW_SUBJECT);
    templateProperties.getFirst().put("oldDesignationOfficial", existingStopPointWorkflow.getDesignationOfficial());
    templateProperties.getFirst().put("newWorkflowComment", newStopPointWorkflow.getWorkflowComment());
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.STOP_POINT_WORKFLOW_RESTART_NOTIFICATION)
        .subject(WorkflowSubject.RESTART_WORKFLOW_SUBJECT)
        .to(examinantMails)
        .templateProperties(templateProperties)
        .build();
  }

  public MailNotification buildWorkflowRestartedCCMail(StopPointWorkflow existingStopPointWorkflow,
      StopPointWorkflow newStopPointWorkflow) {
    List<String> ccMails = new ArrayList<>();
    ccMails.add(existingStopPointWorkflow.getApplicantMail());
    ccMails.addAll(existingStopPointWorkflow.getCcEmails());
    List<Map<String, Object>> templateProperties = buildMailProperties(newStopPointWorkflow,
        WorkflowSubject.RESTART_WORKFLOW_SUBJECT);
    templateProperties.getFirst().put("oldDesignationOfficial", existingStopPointWorkflow.getDesignationOfficial());
    templateProperties.getFirst().put("newWorkflowComment", newStopPointWorkflow.getWorkflowComment());
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION)
        .subject(WorkflowSubject.RESTART_WORKFLOW_SUBJECT)
        .to(ccMails)
        .templateProperties(templateProperties)
        .build();
  }

  List<Map<String, Object>> buildMailProperties(StopPointWorkflow stopPointWorkflow, String title) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", title);
    mailContentProperty.put("designationOfficial", stopPointWorkflow.getDesignationOfficial());
    mailContentProperty.put("sloid", stopPointWorkflow.getSloid());
    mailContentProperty.put("comment", stopPointWorkflow.getWorkflowComment());
    if (stopPointWorkflow.getEndDate() != null) {
      mailContentProperty.put("endDate", DATE_FORMATTER.format(stopPointWorkflow.getEndDate()));
    }
    mailContentProperty.put("url", getUrl(stopPointWorkflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getUrl(StopPointWorkflow stopPointWorkflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + WORKFLOW_URL + stopPointWorkflow.getId();
  }
}
