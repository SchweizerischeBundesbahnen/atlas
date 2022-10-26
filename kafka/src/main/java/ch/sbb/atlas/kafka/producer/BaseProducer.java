package ch.sbb.atlas.kafka.producer;

import ch.sbb.atlas.kafka.model.workflow.event.AtlasEvent;
import ch.sbb.atlas.kafka.topic.KafkaKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseProducer<T extends AtlasEvent> {

  protected final KafkaTemplate<String, Object> kafkaTemplate;

  protected abstract String getTopic();

  public void produceEvent(T event, String kafkaKey) {

    ListenableFuture<SendResult<String, Object>> future =
        kafkaTemplate.send(getTopic(), KafkaKey.MAIL.getValue(), event);

    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onSuccess(SendResult<String, Object> result) {
        log.info("Kafka: Sent message=[{}] with offset=[{}]", event,
            result.getRecordMetadata().offset());
      }

      @Override
      public void onFailure(Throwable ex) {
        log.error("Kafka: Unable to send message=[{}] due to {}: ", event,
            ex.getMessage());
      }
    });
  }

}
