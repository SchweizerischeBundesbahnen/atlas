package ch.sbb.timetable.hearing.configuration;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasAdminFeignConfig extends OAuthClientConfig {

  public AtlasAdminFeignConfig() {
    super("service-to-service");
  }
}
