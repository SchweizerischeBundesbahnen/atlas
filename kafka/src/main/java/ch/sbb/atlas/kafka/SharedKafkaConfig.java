package ch.sbb.atlas.kafka;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SharedKafkaConfig {

  private final KafkaTemplate<?, ?> kafkaTemplate;
  private final List<ConcurrentKafkaListenerContainerFactory<?, ?>> concurrentKafkaListenerContainerFactories;

  @PostConstruct
  void enableObservation() {
    kafkaTemplate.setObservationEnabled(true);
    concurrentKafkaListenerContainerFactories.forEach(
        concurrentKafkaListenerContainerFactory ->
            concurrentKafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true));
  }

}
