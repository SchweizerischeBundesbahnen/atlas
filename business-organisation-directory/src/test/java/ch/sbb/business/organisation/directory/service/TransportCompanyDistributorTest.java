package ch.sbb.business.organisation.directory.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
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

class TransportCompanyDistributorTest {
  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  private TransportCompanyDistributor transportCompanyDistributor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transportCompanyDistributor = new TransportCompanyDistributor(kafkaTemplate);
    transportCompanyDistributor.setTopic("atlas.transport.company");

    CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(
        new SendResult<>(new ProducerRecord<>("topic", 0, "key", "value",
            List.of(new RecordHeader("traceparent", new byte[]{}))),
            new RecordMetadata(new TopicPartition("topic", 0), 0L, 0, 0L, 0, 0)));
    when(kafkaTemplate.send(anyString(), anyString(), any(SharedTransportCompanyModel.class))).thenReturn(future);
  }

  @Test
  void shouldSendKafkaEvent() {
    TransportCompany transportCompany = TransportCompany.builder().build();

    transportCompanyDistributor.pushToKafka(transportCompany);

    verify(kafkaTemplate).send(anyString(), any(), any());
  }
}