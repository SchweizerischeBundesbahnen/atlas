package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionDistributor {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${kafka.atlas.user.administration.topic}")
  private String userPermissionTopic;

  public void pushUserPermissionToKafka(UserAdministrationModel userAdministrationModel) {

    String kafkaKey = userAdministrationModel.getSbbUserId();
    ListenableFuture<SendResult<String, Object>> future =
        kafkaTemplate.send(userPermissionTopic, kafkaKey, userAdministrationModel);

    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onSuccess(SendResult<String, Object> result) {
        log.info("Kafka: Sent message=[{}] with offset=[{}]", userAdministrationModel,
            result.getRecordMetadata().offset());
      }

      @Override
      public void onFailure(Throwable ex) {
        log.error("Kafka: Unable to send message=[{}] due to {}: ", userAdministrationModel,
            ex.getMessage());
      }
    });
  }

}
