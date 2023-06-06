package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserPermissionDistributor extends BaseProducer<UserAdministrationModel> {

  @Value("${kafka.atlas.user.administration.topic}")
  @Getter
  private String topic;

  public UserPermissionDistributor(KafkaTemplate<String, Object> kafkaTemplate) {
    super(kafkaTemplate);
  }

  public void pushUserPermissionToKafka(UserAdministrationModel userAdministrationModel) {
    String kafkaKey = userAdministrationModel.getUserId();
    produceEvent(userAdministrationModel, kafkaKey);
  }

}
