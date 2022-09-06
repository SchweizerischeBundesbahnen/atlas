package ch.sbb.atlas.user.administration.security;

import javax.annotation.PostConstruct;
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
//@KafkaListener(topics = "${spring.kafka.atlas.user.administration.topic}", groupId = "${spring.kafka.atlas.mail.groupId}",
//    topicPartitions = @TopicPartition(topic = "${spring.kafka.atlas.user.administration.topic}",
//        partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")))
public class UserAdministrationLoader {

  @PostConstruct
  void setup(){
    log.info("UserPermissions would be available");
  }

//  @KafkaHandler
//  public void readUserPermissionsFromKafka() {
//    log.info("Receiving all mails, current={}");
//  }
}
