package ch.sbb.atlas.location.config;

import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ch.sbb.atlas.location.module.geo.client")
public class AtlasConfiguration {

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

}
