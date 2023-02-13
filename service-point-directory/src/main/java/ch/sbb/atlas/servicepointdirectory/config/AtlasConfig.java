package ch.sbb.atlas.servicepointdirectory.config;

import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import ch.sbb.atlas.base.service.versioning.service.VersionableServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfig {

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

  @Bean
  public VersionableService versionableService() {
    return new VersionableServiceImpl();
  }
}
