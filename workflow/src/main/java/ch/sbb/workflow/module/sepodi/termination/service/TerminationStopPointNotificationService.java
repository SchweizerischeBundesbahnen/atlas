package ch.sbb.workflow.module.sepodi.termination.service;

import ch.sbb.atlas.api.mail.MailNotification;
import ch.sbb.workflow.mail.MailClient;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointNotificationService {

  private final MailClient mailClient;
  private final TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;

  public void sendStartTerminationNotificationToInfoPlusAndBo(TerminationStopPointWorkflow workflow) {
    MailNotification infoPlusNotification =
        builderNotificationService.buildStartTerminationNotificationMailForInfoPlus(workflow);
    mailClient.sendMailRequest(infoPlusNotification);

    MailNotification applicantNotification =
        builderNotificationService.buildStartTerminationNotificationMailForApplicant(workflow);
    mailClient.sendMailRequest(applicantNotification);
  }

  public void sendTariffStopNotApprovedNotificationToBo(TerminationStopPointWorkflow workflow,
      TerminationDecisionModel decision) {
    MailNotification notification = builderNotificationService.buildTariffStopNotApprovedNotification(workflow,
        decision.getMotivation());
    mailClient.sendMailRequest(notification);
  }

  public void sendTariffStopApprovedNotificationToNova(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildTariffStopApprovedNotification(workflow);
    mailClient.sendMailRequest(notification);
  }

  public void sendAbortNotificationToBoAndInfoPlus(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel) {
    MailNotification notification = builderNotificationService.buildAbortNotificationForBoAndInfoPlus(workflow, abortModel);
    mailClient.sendMailRequest(notification);
  }

  public void sendAbortNotificationToBoInfoPlusAndNova(TerminationStopPointWorkflow workflow, TerminationAbortModel abortModel) {
    MailNotification notification = builderNotificationService.buildAbortNotificationForBoInfoPlusAndNova(workflow, abortModel);
    mailClient.sendMailRequest(notification);
  }

  public void sendTerminationConfirmedNotification(TerminationStopPointWorkflow workflow) {
    MailNotification notification = builderNotificationService.buildTerminationConfirmedNotification(workflow);
    mailClient.sendMailRequest(notification);
  }

}
