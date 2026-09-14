package ch.sbb.importservice.config;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.atlas.s3.service.FileServiceImpl;
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
