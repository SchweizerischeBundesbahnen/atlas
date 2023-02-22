package ch.sbb.atlas.kafka.producer;

import ch.sbb.atlas.kafka.model.AtlasEvent;
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
      String traceparent = new String(result.getProducerRecord().headers().headers("traceparent").iterator().next().value());
      if (exception == null) {
        log.info("Kafka, traceparent={}: Sent message=[{}] with offset=[{}]", traceparent, event,
            result.getRecordMetadata().offset());
      } else {
        log.error("Kafka, traceparent={}: Unable to send message=[{}] due to {}: ", traceparent, event,
            exception.getMessage());
      }
    });
  }

}
