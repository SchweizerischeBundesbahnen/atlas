package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
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

  public void sendTariffStopApprovedNotificationToNovaAndBo(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildTariffStopApprovedNotification(workflow);
    mailProducerService.produceMailNotification(notification);

    notification.setTo(List.of(workflow.getApplicantMail()));
    mailProducerService.produceMailNotification(notification);
  }

  public void sendCancelNotificationToInfoPlusAndBoAndNova(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildCancelNotification(workflow);
    if (workflow.getInfoPlusDecision() != null) {
      notification.setTo(List.of(terminationExaminants.getNova().getEmail(), terminationExaminants.getInfoPlus().getEmail(),
          workflow.getApplicantMail()));
    }
    mailProducerService.produceMailNotification(notification);
  }

  public void sendCancelNotificationToBoAndInfoPlus(TerminationStopPointWorkflow terminationWorkflow) {

  }

  public void sendCancelNotificationToBoInfoPlusAndNova(TerminationStopPointWorkflow terminationWorkflow) {

  }
}
