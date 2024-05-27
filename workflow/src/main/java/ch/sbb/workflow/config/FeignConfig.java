package ch.sbb.workflow.config;

import ch.sbb.atlas.api.client.AtlasApiFeignClientsConfig;
import ch.sbb.workflow.client.RetreiveMessageErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(AtlasApiFeignClientsConfig.class)
@Configuration
public class FeignConfig {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new RetreiveMessageErrorDecoder();
  }

}
