package ch.sbb.workflow.kafka;

import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.service.lidi.LineWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkflowNotificationService {

    private final MailProducerService mailProducerService;

    private final LineWorkflowService lineWorkflowService;

    public void sendEventToMail(Workflow workflow) {
        if (WorkflowType.LINE == workflow.getWorkflowType()) {
            mailProducerService.produceMailNotification(getMailNotificationByStatus(workflow));
        }
    }

    private MailNotification getMailNotificationByStatus(Workflow workflow) {
        return switch (workflow.getStatus()) {
            case STARTED -> lineWorkflowService.buildWorkflowStartedMailNotification(workflow);
            case APPROVED, REJECTED -> lineWorkflowService.buildWorkflowCompletedMailNotification(workflow);
            default -> throw new IllegalArgumentException();
        };
    }

}
