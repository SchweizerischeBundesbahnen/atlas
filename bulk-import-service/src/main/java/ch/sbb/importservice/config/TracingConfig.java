package ch.sbb.importservice.config;

import org.springframework.batch.core.configuration.annotation.BatchObservabilityBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

  @Bean
  public BatchObservabilityBeanPostProcessor batchObservabilityBeanPostProcessor() {
    return new BatchObservabilityBeanPostProcessor();
  }

}
