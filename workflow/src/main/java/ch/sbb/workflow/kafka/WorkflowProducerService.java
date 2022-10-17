package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkflowProducerService extends BaseProducer<WorkflowEvent> {

  @Value("${kafka.atlas.workflow.topic}")
  private String topic;

  public WorkflowProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
    super(kafkaTemplate);
  }

  @Override
  protected String getTopic() {
    return this.topic;
  }

  public void produceWorkflowNotification(WorkflowEvent workflowEvent) {
    produceEvent(workflowEvent);
  }

}
