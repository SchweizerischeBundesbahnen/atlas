package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StopPointWorkflowNotificationService {

    private final MailProducerService mailProducerService;
    private final StopPointWorkflowBuilderNotificationService builderNotificationService;

    public void sendStartStopPointWorkflowMail(StopPointWorkflow stopPointWorkflow) {
        MailNotification startedExaminantMailNotification =
            builderNotificationService.buildWorkflowStartedExaminantMail(stopPointWorkflow);
        mailProducerService.produceMailNotification(startedExaminantMailNotification);

        MailNotification buildWorkflowStartedCCMailNotification =
            builderNotificationService.buildWorkflowStartedCCMail(stopPointWorkflow);
        mailProducerService.produceMailNotification(buildWorkflowStartedCCMailNotification);
    }

    public void sendStartToAddedExaminant(StopPointWorkflow stopPointWorkflow, List<String> examinantMails) {
        MailNotification startedExaminantMailNotification =
            builderNotificationService.buildWorkflowStartedExaminantMail(stopPointWorkflow, examinantMails);
        mailProducerService.produceMailNotification(startedExaminantMailNotification);
    }

    public void sendRejectStopPointWorkflowMail(StopPointWorkflow workflow, String rejectComment) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowRejectMail(workflow, rejectComment);
        mailProducerService.produceMailNotification(mailNotification);
    }

    public void sendPinCodeMail(StopPointWorkflow workflow, String examinantMail, String pinCode) {
        MailNotification mailNotification = builderNotificationService.buildPinCodeMail(workflow, examinantMail, pinCode);
        mailProducerService.produceMailNotification(mailNotification);
    }

    public void sendApprovedStopPointWorkflowMail(StopPointWorkflow workflow) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowApprovedMail(workflow);
        mailProducerService.produceMailNotification(mailNotification);
    }

    public void sendCanceledStopPointWorkflowMail(StopPointWorkflow workflow, String cancelComment) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowCanceledMail(workflow, cancelComment);
        mailProducerService.produceMailNotification(mailNotification);
    }

    public void sendRestartStopPointWorkflowMail(StopPointWorkflow existingStopPointWorkflow,
        StopPointWorkflow newStopPointWorkflow) {
        MailNotification restartedMailNotification =
            builderNotificationService.buildWorkflowRestartedMail(existingStopPointWorkflow, newStopPointWorkflow);
        mailProducerService.produceMailNotification(restartedMailNotification);
        MailNotification restartedCCMailNotification =
            builderNotificationService.buildWorkflowRestartedCCMail(existingStopPointWorkflow, newStopPointWorkflow);
        mailProducerService.produceMailNotification(restartedCCMailNotification);
    }

}
