package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JourneyPoiConfig extends OAuthClientConfig {

  public JourneyPoiConfig() {
    super("journeyPoi");
  }

}