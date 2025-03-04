package ch.sbb.importservice.config;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.amazon.service.FileServiceImpl;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfiguration {

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

  @Bean
  public FileService fileService() {
    return new FileServiceImpl();
  }

}
