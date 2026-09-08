package ch.sbb.workflow.config;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.atlas.s3.service.FileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfig {

  @Bean
  public FileService fileService() {
    return new FileServiceImpl();
  }

}
