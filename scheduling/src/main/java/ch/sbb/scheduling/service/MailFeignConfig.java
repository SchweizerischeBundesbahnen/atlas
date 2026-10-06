package ch.sbb.scheduling.service;

import ch.sbb.atlas.api.mail.MailOAuthClientConfig;

public class MailFeignConfig extends MailOAuthClientConfig {

  public MailFeignConfig() {
    super("azure");
  }

}
