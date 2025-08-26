package ch.sbb.workflow.module.sepodi.termination.service;

import static ch.sbb.workflow.module.sepodi.termination.service.TerminationWorkflowSubject.STOP_POINT_TERMINATION_CONFIRMED_SUBJECT;

import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.mail.BaseNotificationService;
import ch.sbb.workflow.module.sepodi.termination.TerminationWorkflowHelper;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationExaminants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointWorkflowBuilderNotificationService extends BaseNotificationService {

  public static final String TERMINATION_WORKFLOW_URL = "service-point-directory/termination-workflows/";

  private final TerminationExaminants terminationExaminants;

  public MailNotification buildStartTerminationNotificationMailForInfoPlus(TerminationStopPointWorkflow workflow) {
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION)
        .subject(TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT)
        .to(List.of(terminationExaminants.getInfoPlus().getEmail()))
        .templateProperties(buildMailProperties(workflow, TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT))
        .build();
  }

  public MailNotification buildTariffStopNotApprovedNotification(TerminationStopPointWorkflow workflow, String motivation) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = getCommonMailContentProperty(
        TerminationWorkflowSubject.TARIFF_STOP_NOT_APPROVED_SUBJECT, workflow);
    mailContentProperty.put("motivation", motivation);
    mailProperties.add(mailContentProperty);
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.TARIFF_STOP_NOT_APPROVED_NOTIFICATION)
        .subject(TerminationWorkflowSubject.TARIFF_STOP_NOT_APPROVED_SUBJECT)
        .to(List.of(workflow.getApplicantMail()))
        .templateProperties(mailProperties)
        .build();
  }

  public MailNotification buildTariffStopApprovedNotification(TerminationStopPointWorkflow workflow) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = getCommonMailContentProperty(
        TerminationWorkflowSubject.TARIFF_STOP_APPROVED_SUBJECT, workflow);
    mailContentProperty.put("url", getWorkflowUrl(workflow));
    mailContentProperty.put("boTerminationDate", DATE_FORMATTER.format(workflow.getBoTerminationDate()));
    mailContentProperty.put("infoPlusTerminationDate", DATE_FORMATTER.format(workflow.getInfoPlusTerminationDate()));
    mailContentProperty.put("infoPlusMotivation", workflow.getInfoPlusDecision().getMotivation());
    mailProperties.add(mailContentProperty);

    return MailNotification.builder()
        .from(from)
        .mailType(MailType.TARIFF_STOP_APPROVED_NOTIFICATION)
        .subject(TerminationWorkflowSubject.TARIFF_STOP_APPROVED_SUBJECT)
        .to(List.of(terminationExaminants.getNova().getEmail()))
        .templateProperties(mailProperties)
        .build();
  }

  public MailNotification buildAbortNotificationForBoAndInfoPlus(TerminationStopPointWorkflow workflow,
      TerminationAbortModel abortModel) {
    List<String> emailsTo = List.of(terminationExaminants.getInfoPlus().getEmail(),
        workflow.getApplicantMail());
    return buildAbortNotification(workflow, abortModel, emailsTo);
  }

  public MailNotification buildAbortNotificationForBoInfoPlusAndNova(TerminationStopPointWorkflow workflow,
      TerminationAbortModel abortModel) {
    List<String> emailsTo = List.of(terminationExaminants.getInfoPlus().getEmail(),
        workflow.getApplicantMail(), terminationExaminants.getNova().getEmail());
    return buildAbortNotification(workflow, abortModel, emailsTo);
  }

  MailNotification buildAbortNotification(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel,
      List<String> emailsTo) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = getCommonMailContentProperty(
        TerminationWorkflowSubject.ABORT_TERMINATION_SUBJECT, workflow);
    mailContentProperty.put("abortComment", abortModel.getAbortComment());
    mailContentProperty.put("url", getWorkflowUrl(workflow));
    mailProperties.add(mailContentProperty);
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.ABORT_TERMINATION_NOTIFICATION)
        .subject(TerminationWorkflowSubject.ABORT_TERMINATION_SUBJECT)
        .to(emailsTo)
        .templateProperties(mailProperties)
        .build();
  }

  public MailNotification buildTerminationConfirmedNotification(TerminationStopPointWorkflow workflow) {
    List<String> emailsTo = List.of(terminationExaminants.getInfoPlus().getEmail(),
        terminationExaminants.getNova().getEmail(),
        workflow.getApplicantMail());
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = getCommonMailContentProperty(STOP_POINT_TERMINATION_CONFIRMED_SUBJECT, workflow);
    mailContentProperty.put("infoPlusTerminationDate", DATE_FORMATTER.format(workflow.getInfoPlusTerminationDate().plusDays(1)));
    mailContentProperty.put("url", getWorkflowUrl(workflow));
    mailProperties.add(mailContentProperty);
    return MailNotification.builder()
        .from(from)
        .mailType(MailType.STOP_POINT_TERMINATION_CONFIRMED_NOTIFICATION)
        .subject(STOP_POINT_TERMINATION_CONFIRMED_SUBJECT)
        .to(emailsTo)
        .templateProperties(mailProperties)
        .build();
  }

  private static Map<String, Object> getCommonMailContentProperty(String abortTerminationSubject,
      TerminationStopPointWorkflow workflow) {
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("title", abortTerminationSubject);
    mailContentProperty.put("designationOfficial", workflow.getDesignationOfficial());
    mailContentProperty.put("sloid", workflow.getSloid());
    return mailContentProperty;
  }

  private List<Map<String, Object>> buildMailProperties(TerminationStopPointWorkflow workflow, String title) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = getCommonMailContentProperty(title, workflow);
    mailContentProperty.put("url", getWorkflowUrl(workflow));
    mailContentProperty.put("terminationDate", calculateTerminationDate(workflow));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String getWorkflowUrl(TerminationStopPointWorkflow workflow) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) + TERMINATION_WORKFLOW_URL + workflow.getId();
  }

  private String calculateTerminationDate(TerminationStopPointWorkflow workflow) {
    return DATE_FORMATTER.format(TerminationWorkflowHelper.getTerminationDate(workflow));
  }

}
