package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.entity.WorkflowType;
import ch.sbb.workflow.service.WorkflowLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkflowNotificationService {

  private final WorkflowProducerService workflowProducerService;

  private final MailProducerService mailProducerService;

  private final WorkflowLineService workflowLineService;

  public void sendEventToMail(Workflow workflow) {
    MailNotification mailNotification = null;
    if (WorkflowType.LINE == workflow.getWorkflowType()) {
      mailNotification = workflowLineService.buildMailNotification(workflow);
    }
    mailProducerService.produceMailNotification(mailNotification);
  }

  public void sendEventToLidi(Workflow workflow) {
    WorkflowEvent workflowEvent = WorkflowEvent.builder().workflowId(workflow.getId()).build();
    workflowProducerService.produceWorkflowNotification(workflowEvent);
  }
}
