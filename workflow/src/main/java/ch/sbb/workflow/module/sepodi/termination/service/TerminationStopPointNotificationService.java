package ch.sbb.workflow.module.sepodi.termination.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointNotificationService {

  private final MailProducerService mailProducerService;
  private final TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;

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
    MailNotification notification = builderNotificationService.buildAbortNotificationForBoAndInfoPlus(workflow, abortModel);
    mailProducerService.produceMailNotification(notification);
  }

  public void sendAbortNotificationToBoInfoPlusAndNova(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel) {
    MailNotification notification = builderNotificationService.buildAbortNotificationForBoInfoPlusAndNova(workflow, abortModel);
    mailProducerService.produceMailNotification(notification);
  }

  public void sendTerminationConfirmedNotification(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildTerminationConfirmedNotification(workflow);
    mailProducerService.produceMailNotification(notification);
  }

}
