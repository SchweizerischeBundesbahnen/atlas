package ch.sbb.timetable.hearing.configuration;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.amazon.service.FileServiceImpl;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.transport.company.SharedTransportCompanyConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({SharedTransportCompanyConfig.class})
@Configuration
public class AtlasConfig {

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

  @Bean
  public FileService fileService() {
    return new FileServiceImpl();
  }
}
