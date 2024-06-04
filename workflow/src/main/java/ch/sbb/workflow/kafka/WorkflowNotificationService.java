package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.LineWorkflow;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.service.lidi.LineWorkflowService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkflowNotificationService {

    private final MailProducerService mailProducerService;

    private final LineWorkflowService lineWorkflowService;

    public void sendEventToMail(LineWorkflow lineWorkflow) {
        if (WorkflowType.LINE == lineWorkflow.getWorkflowType()) {
            mailProducerService.produceMailNotification(getMailNotificationByStatus(lineWorkflow));
        }
    }

    public void sendStartStopPointWorkflowMail(StopPointWorkflow stopPointWorkflow) {
        Set<String> mails = stopPointWorkflow.getExaminants().stream().map(Person::getMail).collect(Collectors.toSet());
        mails.addAll(stopPointWorkflow.getCcEmails());
        //TODO: Send Email
    }

    private MailNotification getMailNotificationByStatus(LineWorkflow lineWorkflow) {
        return switch (lineWorkflow.getStatus()) {
            case STARTED -> lineWorkflowService.buildWorkflowStartedMailNotification(lineWorkflow);
            case APPROVED, REJECTED -> lineWorkflowService.buildWorkflowCompletedMailNotification(lineWorkflow);
            default -> throw new IllegalArgumentException();
        };
    }

    public void sendRejectPointWorkflowMail(StopPointWorkflow workflow) {
        //TODO: Send Email
    }
}
