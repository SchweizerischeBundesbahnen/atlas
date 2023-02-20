package ch.sbb.atlas.kafka.producer;

import ch.sbb.atlas.kafka.model.workflow.event.AtlasEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseProducer<T extends AtlasEvent> {

  protected final KafkaTemplate<String, Object> kafkaTemplate;

  protected abstract String getTopic();

  public void produceEvent(T event, String kafkaKey) {
    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(getTopic(), kafkaKey, event);
    future.whenComplete((result, exception) -> {
      if (exception == null) {
        log.info("Kafka: Sent message=[{}] with offset=[{}]", event,
            result.getRecordMetadata().offset());
      } else {
        log.error("Kafka: Unable to send message=[{}] due to {}: ", event,
            exception.getMessage());
      }
    });
  }

}
