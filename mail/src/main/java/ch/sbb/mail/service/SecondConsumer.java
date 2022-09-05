package ch.sbb.mail.service;

import ch.sbb.atlas.model.mail.MailNotification;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${spring.kafka.atlas.mail.topic}", groupId = "${spring.kafka.atlas.mail.groupId}",
    topicPartitions = @TopicPartition(topic = "${spring.kafka.atlas.mail.topic}",
        partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")))
public class SecondConsumer {

  @KafkaHandler
  public void receiveMailSendingRequest(@Valid MailNotification mailNotification) {
    log.info("Receiving on a second consumer, current={}", mailNotification);
  }

}
