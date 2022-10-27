package ch.sbb.line.directory.kafka;

import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
import ch.sbb.line.directory.service.workflow.LineWorkflowProcessingService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${kafka.atlas.workflow.topic}", groupId = "${kafka.atlas.workflow.groupId}")
public class WorkflowListener {

  private final LineWorkflowProcessingService lineWorkflowProcessingService;

  @KafkaHandler
  public void receiveLineWorkflowNotification(@Valid LineWorkflowEvent lineWorkflowEvent) {
    log.info("Consumed: {}", lineWorkflowEvent);
    lineWorkflowProcessingService.processLineWorkflow(lineWorkflowEvent);
  }

}
