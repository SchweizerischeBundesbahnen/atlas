package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointNotificationService {

  private final MailProducerService mailProducerService;
  private final TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;

  public void sendStartTerminationNotificationToInfoPlus(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildStartTerminationNotificationMailForInfoPlus(workflow);
    mailProducerService.produceMailNotification(notification);
  }

  public void sendStartConfirmationTerminationNotificationToApplicantMail(TerminationStopPointWorkflow workflow) {
    //add mailTemplate2
  }

  public void sendCancelNotificationToApplicationMail(TerminationStopPointWorkflow terminationWorkflow,
      TerminationDecisionModel decisionModel) {
    //send notification
  }

  public void sendTerminationApprovedNotificationToNova(TerminationStopPointWorkflow terminationWorkflow,
      TerminationDecisionModel decisionModel) {
    //send notification
  }
}
