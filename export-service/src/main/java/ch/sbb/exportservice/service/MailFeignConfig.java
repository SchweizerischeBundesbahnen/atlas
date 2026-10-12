package ch.sbb.exportservice.service;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;

public class MailFeignConfig extends OAuthClientConfig {

  public MailFeignConfig() {
    super("service-to-service");
  }

}
