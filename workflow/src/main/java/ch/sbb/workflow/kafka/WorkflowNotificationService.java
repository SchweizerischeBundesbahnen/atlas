package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.service.lidi.LineWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkflowNotificationService {

  private final WorkflowProducerService workflowProducerService;

  private final MailProducerService mailProducerService;

  private final LineWorkflowService lineWorkflowService;

  public void sendEventToMail(Workflow workflow) {
    MailNotification mailNotification;
    if (WorkflowType.LINE == workflow.getWorkflowType()) {
      mailNotification = lineWorkflowService.buildMailNotification(workflow);
      mailProducerService.produceMailNotification(mailNotification);
    }
  }

  public void sendEventToLidi(Workflow workflow) {
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(workflow.getId())
        .businessObjectId(workflow.getBusinessObjectId())
        .workflowStatus(workflow.getStatus())
        .build();
    workflowProducerService.produceWorkflowNotification(workflowEvent);
  }
}
