package ch.sbb.workflow.module.sepodi.hearing.mail;

import ch.sbb.atlas.api.mail.MailNotification;
import ch.sbb.workflow.mail.MailClient;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowBuilderNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StopPointWorkflowNotificationService {

    private final MailClient mailClient;
    private final StopPointWorkflowBuilderNotificationService builderNotificationService;

    public void sendStartStopPointWorkflowMail(StopPointWorkflow stopPointWorkflow) {
        MailNotification startedExaminantMailNotification =
            builderNotificationService.buildWorkflowStartedExaminantMail(stopPointWorkflow);
        log.info("Sending mail for StopPointWorkflowId={}. Start mail={}", stopPointWorkflow.getId(),
            startedExaminantMailNotification);
        mailClient.sendMailRequest(startedExaminantMailNotification);

        MailNotification buildWorkflowStartedCCMailNotification =
            builderNotificationService.buildWorkflowStartedCCMail(stopPointWorkflow);
        log.info("Sending mail for StopPointWorkflowId={}. Start CC mail={}", stopPointWorkflow.getId(),
            buildWorkflowStartedCCMailNotification);
        mailClient.sendMailRequest(buildWorkflowStartedCCMailNotification);
    }

    public void sendStartToAddedExaminant(StopPointWorkflow stopPointWorkflow, List<String> examinantMails) {
        MailNotification startedExaminantMailNotification =
            builderNotificationService.buildWorkflowStartedExaminantMail(stopPointWorkflow, examinantMails);
        log.info("Sending mail for StopPointWorkflowId={}. Start mail for added examinant={}",
            stopPointWorkflow.getId(), startedExaminantMailNotification);
        mailClient.sendMailRequest(startedExaminantMailNotification);
    }

    public void sendRejectStopPointWorkflowMail(StopPointWorkflow workflow, String rejectComment) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowRejectMail(workflow, rejectComment);
        log.info("Sending mail for StopPointWorkflowId={}. Reject mail={}", workflow.getId(), mailNotification);
        mailClient.sendMailRequest(mailNotification);
    }

    public void sendPinCodeMail(StopPointWorkflow workflow, String examinantMail, String pinCode) {
        MailNotification mailNotification = builderNotificationService.buildPinCodeMail(workflow, examinantMail, pinCode);
        mailClient.sendMailRequest(mailNotification);
    }

    public void sendApprovedStopPointWorkflowMail(StopPointWorkflow workflow) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowApprovedMail(workflow);
        log.info("Sending mail for StopPointWorkflowId={}. Approved mail={}", workflow.getId(), mailNotification);
        mailClient.sendMailRequest(mailNotification);
    }

    public void sendCanceledStopPointWorkflowMail(StopPointWorkflow workflow, String cancelComment) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowCanceledMail(workflow, cancelComment);
        log.info("Sending mail for StopPointWorkflowId={}. Cancel mail={}", workflow.getId(), mailNotification);
        mailClient.sendMailRequest(mailNotification);
    }

    public void sendRestartStopPointWorkflowMail(StopPointWorkflow existingStopPointWorkflow,
        StopPointWorkflow newStopPointWorkflow) {
        MailNotification restartedMailNotification =
            builderNotificationService.buildWorkflowRestartedMail(existingStopPointWorkflow, newStopPointWorkflow);
        log.info("Sending mail for StopPointWorkflowId={}. Restart mail={}", newStopPointWorkflow.getId(),
            restartedMailNotification);
        mailClient.sendMailRequest(restartedMailNotification);

        MailNotification restartedCCMailNotification =
            builderNotificationService.buildWorkflowRestartedCCMail(existingStopPointWorkflow, newStopPointWorkflow);
        log.info("Sending mail for StopPointWorkflowId={}. Restart cc mail={}", newStopPointWorkflow.getId(),
            restartedCCMailNotification);
        mailClient.sendMailRequest(restartedCCMailNotification);
    }

}
