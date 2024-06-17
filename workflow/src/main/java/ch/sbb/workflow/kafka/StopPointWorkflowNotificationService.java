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
    private final StopPointWorkflowBuilderNotificationService builderNotificationService;

    public void sendStartStopPointWorkflowMail(StopPointWorkflow stopPointWorkflow) {
        MailNotification startedExaminantMailNotification =
            builderNotificationService.buildWorkflowStartedExaminantMail(stopPointWorkflow);
        mailProducerService.produceMailNotification(startedExaminantMailNotification);
        MailNotification buildWorkflowStartedCCMailNotification =
            builderNotificationService.buildWorkflowStartedCCMail(stopPointWorkflow);
        mailProducerService.produceMailNotification(buildWorkflowStartedCCMailNotification);
    }

    public void sendRejectStopPointWorkflowMail(StopPointWorkflow workflow) {
        MailNotification mailNotification = builderNotificationService.buildWorkflowRejectMail(workflow);
        mailProducerService.produceMailNotification(mailNotification);
    }
}
