package ch.sbb.atlas.servicepointdirectory.config;

import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.business.organisation.SharedBusinessOrganisationConfig;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.versioning.service.VersionableServiceImpl;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(SharedBusinessOrganisationConfig.class)
@EnableFeignClients(basePackages = {
    "ch.sbb.atlas.servicepointdirectory.service.georeference", "ch.sbb.atlas.api.client.location"})
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

  @Bean
  public LocationService locationService(LocationClient locationClient) {
    return new LocationService(locationClient);
  }

}
