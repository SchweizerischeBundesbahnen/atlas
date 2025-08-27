package ch.sbb.workflow.module.lidi.line.mail;

import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.module.lidi.line.entity.LineWorkflow;
import ch.sbb.workflow.module.lidi.line.service.LineWorkflowBuilderNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineWorkflowNotificationService {

  private final MailProducerService mailProducerService;

  private final LineWorkflowBuilderNotificationService lineWorkflowBuilderNotificationService;

  public void sendEventToMail(LineWorkflow lineWorkflow) {
    if (WorkflowType.LINE == lineWorkflow.getWorkflowType()) {
      mailProducerService.produceMailNotification(
          lineWorkflowBuilderNotificationService.buildWorkflowMailNotification(lineWorkflow));
    }
  }

}
