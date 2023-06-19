package ch.sbb.line.directory.configuration;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.amazon.service.FileServiceImpl;
import ch.sbb.atlas.business.organisation.SharedBusinessOrganisationConfig;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.versioning.service.VersionableServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(SharedBusinessOrganisationConfig.class)
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
