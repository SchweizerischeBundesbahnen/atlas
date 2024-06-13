package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StopPointWorkflowNotificationService {

    private final MailProducerService mailProducerService;
    private final StopPointWorkflowBuilderNotificationService stopPointWorkflowBuilderNotificationService;

    public void sendStartStopPointWorkflowMail(StopPointWorkflow stopPointWorkflow) {
        MailNotification startedExaminantMailNotification =
            stopPointWorkflowBuilderNotificationService.buildWorkflowStartedExaminantMailNotification(
                stopPointWorkflow);
        mailProducerService.produceMailNotification(startedExaminantMailNotification);
        MailNotification buildWorkflowStartedCCMailNotification =
            stopPointWorkflowBuilderNotificationService.buildWorkflowStartedCCMailNotification(stopPointWorkflow);
            mailProducerService.produceMailNotification(buildWorkflowStartedCCMailNotification);

    }

    public void sendRejectPointWorkflowMail(StopPointWorkflow workflow) {
        MailNotification mailNotification = stopPointWorkflowBuilderNotificationService.buildWorkflowRejectExaminantMailNotification(
            workflow);
        mailProducerService.produceMailNotification(mailNotification);
    }
}
