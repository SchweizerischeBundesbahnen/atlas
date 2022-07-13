package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.mail.MailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailClient {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void produceMailNotification(MailNotification mailNotification) {
    ListenableFuture<SendResult<String, Object>> future =
        kafkaTemplate.send("atlas.mail", "mail", mailNotification);

    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onSuccess(SendResult<String, Object> result) {
        log.info("Kafka: Sent message=[{}] with offset=[{}]", mailNotification,
            result.getRecordMetadata().offset());
      }

      @Override
      public void onFailure(Throwable ex) {
        log.error("Kafka: Unable to send message=[{}] due to {}: ", mailNotification,
            ex.getMessage());
      }
    });
  }
}
