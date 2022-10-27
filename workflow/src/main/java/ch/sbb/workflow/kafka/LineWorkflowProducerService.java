package ch.sbb.workflow.kafka;

import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import ch.sbb.atlas.kafka.topic.KafkaKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LineWorkflowProducerService extends BaseProducer<LineWorkflowEvent> {

  @Value("${kafka.atlas.workflow.topic}")
  @Getter
  private String topic;

  public LineWorkflowProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
    super(kafkaTemplate);
  }

  public void produceWorkflowNotification(LineWorkflowEvent lineWorkflowEvent) {
    produceEvent(lineWorkflowEvent, KafkaKey.WORKFLOW.getValue());
  }

}
