package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointNotificationService {

  private final MailProducerService mailProducerService;
  private final TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;
  private final TerminationExaminants terminationExaminants;

  public void sendStartTerminationNotificationToInfoPlusAndBo(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildStartTerminationNotificationMailForInfoPlus(workflow);
    mailProducerService.produceMailNotification(notification);

    notification.setTo(List.of(workflow.getApplicantMail()));
    mailProducerService.produceMailNotification(notification);
  }

  public void sendTariffStopNotApprovedNotificationToBo(TerminationStopPointWorkflow workflow,
      TerminationDecisionModel decision) {
    MailNotification notification = builderNotificationService.buildTariffStopNotApprovedNotification(workflow,
        decision.getMotivation());
    mailProducerService.produceMailNotification(notification);
  }

  public void sendTariffStopApprovedNotificationToNova(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildTariffStopApprovedNotification(workflow);
    mailProducerService.produceMailNotification(notification);
  }

  public void sendAbortNotificationToBoAndInfoPlus(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel) {
    List<String> emailsTo = List.of(terminationExaminants.getInfoPlus().getEmail(),
        workflow.getApplicantMail());
    sendAbortNotificationToInfoPlusAndBoAndNova(workflow, abortModel, emailsTo);
  }

  public void sendAbortNotificationToBoInfoPlusAndNova(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel) {
    List<String> emailsTo = List.of(terminationExaminants.getInfoPlus().getEmail(),
        workflow.getApplicantMail(), terminationExaminants.getNova().getEmail());
    sendAbortNotificationToInfoPlusAndBoAndNova(workflow, abortModel, emailsTo);
  }

  void sendAbortNotificationToInfoPlusAndBoAndNova(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel,
      List<String> emailsTo) {
    MailNotification notification = builderNotificationService.buildAbortNotification(workflow, abortModel);
    notification.setTo(emailsTo);
    mailProducerService.produceMailNotification(notification);
  }

}
