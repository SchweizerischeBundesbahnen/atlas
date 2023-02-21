package ch.sbb.mail.config;

import ch.sbb.atlas.kafka.SharedKafkaConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Import(SharedKafkaConfig.class)
public class KafkaConfig {

  private final KafkaProperties kafkaProperties;

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    return new DefaultKafkaConsumerFactory<>(props);
  }

}
