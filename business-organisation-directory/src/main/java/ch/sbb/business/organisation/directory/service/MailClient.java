package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailClient extends BaseProducer<MailNotification> {

  @Value("${kafka.atlas.mail.topic}")
  @Getter
  private String topic;

  public MailClient(KafkaTemplate<String, Object> kafkaTemplate) {
    super(kafkaTemplate);
  }

  public void produceMailNotification(MailNotification mailNotification) {

    produceEvent(mailNotification);
  }

}
