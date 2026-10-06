package ch.sbb.atlas.api.mail;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;
import feign.Request;
import java.time.Duration;
import org.springframework.context.annotation.Bean;

public class MailOAuthClientConfig extends OAuthClientConfig {

  private static final Duration TIMEOUT = Duration.ofSeconds(5);

  protected MailOAuthClientConfig(String clientId) {
    super(clientId);
  }

  @Bean
  public Request.Options mailRequestOptions() {
    return new Request.Options(TIMEOUT, TIMEOUT, true);
  }

}
