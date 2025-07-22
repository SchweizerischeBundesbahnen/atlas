package ch.sbb.atlas.model.controller;

import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.mock.MockConsumerFactory;
import org.springframework.kafka.mock.MockProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@TestConfiguration
public class MockKafkaConfig {

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    return new MockConsumerFactory<>(() -> new MockConsumer<>(OffsetResetStrategy.EARLIEST));
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return new MockProducerFactory<>(() -> new MockProducer<>(true, null, new StringSerializer(), new JsonSerializer<>()));
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

}