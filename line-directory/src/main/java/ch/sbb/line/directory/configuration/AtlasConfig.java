package ch.sbb.line.directory.configuration;

import ch.sbb.atlas.base.service.amazon.service.FileService;
import ch.sbb.atlas.base.service.amazon.service.FileServiceImpl;
import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import ch.sbb.atlas.base.service.versioning.service.VersionableServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfig {

  @Bean
  public VersionableService versionableService() {
    return new VersionableServiceImpl();
  }

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

  @Bean
  public FileService fileService() {
    return new FileServiceImpl();
  }
}
