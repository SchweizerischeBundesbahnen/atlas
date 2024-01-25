package ch.sbb.atlas.servicepointdirectory.config;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthFeignConfig extends OAuthClientConfig {

  public OAuthFeignConfig() {
    super("azure");
  }

}
