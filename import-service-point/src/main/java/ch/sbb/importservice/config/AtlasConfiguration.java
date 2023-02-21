package ch.sbb.importservice.config;

import ch.sbb.atlas.base.service.amazon.service.FileService;
import ch.sbb.atlas.base.service.amazon.service.FileServiceImpl;
import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
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
