package ch.sbb.line.directory.kafka;

import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
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

  @KafkaHandler
  public void receiveWorkflowNotification(@Valid WorkflowEvent workflowEvent) {
    log.info("Consumed: {}", workflowEvent);
  }

}
