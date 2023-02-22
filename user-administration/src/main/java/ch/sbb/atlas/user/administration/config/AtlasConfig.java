package ch.sbb.atlas.user.administration.config;

import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfig {

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }
}
