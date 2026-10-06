package ch.sbb.exportservice.service;

import ch.sbb.atlas.api.mail.MailOAuthClientConfig;

public class MailFeignConfig extends MailOAuthClientConfig {

  public MailFeignConfig() {
    super("atlasAdmin");
  }

}
