package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.LineWorkflow;
import ch.sbb.workflow.service.lidi.LineWorkflowBuilderNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineWorkflowNotificationService {

  private final MailProducerService mailProducerService;

  private final LineWorkflowBuilderNotificationService lineWorkflowBuilderNotificationService;

  public void sendEventToMail(LineWorkflow lineWorkflow) {
    if (WorkflowType.LINE == lineWorkflow.getWorkflowType()) {
      mailProducerService.produceMailNotification(getMailNotificationByStatus(lineWorkflow));
    }
  }

  private MailNotification getMailNotificationByStatus(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED -> lineWorkflowBuilderNotificationService.buildWorkflowStartedMailNotification(lineWorkflow);
      case APPROVED -> lineWorkflowBuilderNotificationService.buildWorkflowApprovedMailNotification(lineWorkflow);
      case REJECTED -> lineWorkflowBuilderNotificationService.buildWorkflowCompletedMailNotification(lineWorkflow);
      default -> throw new IllegalArgumentException();
    };
  }

}
