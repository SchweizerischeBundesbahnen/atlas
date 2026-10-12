package ch.sbb.timetable.hearing.mail;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;

public class MailFeignConfig extends OAuthClientConfig {

  public MailFeignConfig() {
    super("service-to-service");
  }

}
