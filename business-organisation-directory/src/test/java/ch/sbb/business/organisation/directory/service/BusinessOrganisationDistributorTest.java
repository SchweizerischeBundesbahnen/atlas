package ch.sbb.business.organisation.directory.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationUpdate;
import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

class BusinessOrganisationDistributorTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  private BusinessOrganisationDistributor businessOrganisationDistributor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    businessOrganisationDistributor = new BusinessOrganisationDistributor(kafkaTemplate);
    businessOrganisationDistributor.setTopic("atlas.business.organisation");

    CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(
        new SendResult<>(new ProducerRecord<>("topic", 0, "key", "value",
            List.of(new RecordHeader("traceparent", new byte[]{}))),
            new RecordMetadata(new TopicPartition("topic", 0), 0L, 0, 0L, 0, 0)));
    when(kafkaTemplate.send(anyString(), anyString(), any(SharedBusinessOrganisationUpdate.class))).thenReturn(future);
  }

  @Test
  void shouldSendKafkaEvent() {
    BusinessOrganisationVersion businessOrganisationVersion = BusinessOrganisationVersion.builder()
        .id(123L)
        .descriptionDe("Boss BO")
        .status(Status.VALIDATED).build();

    businessOrganisationDistributor.saveToDistributedServices(businessOrganisationVersion);

    verify(kafkaTemplate).send(anyString(), any(), any());
  }
}