package ch.sbb.importservice.config.mail;

import ch.sbb.atlas.api.mail.MailOAuthClientConfig;

public class MailFeignConfig extends MailOAuthClientConfig {

  public MailFeignConfig() {
    super("azure");
  }

}
