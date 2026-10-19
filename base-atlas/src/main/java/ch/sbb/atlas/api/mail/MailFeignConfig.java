package ch.sbb.atlas.api.mail;

import ch.sbb.atlas.oauth.client.OAuthClientConfig;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(MailRetryConfig.class)
public class MailFeignConfig extends OAuthClientConfig {

  public MailFeignConfig() {
    super("service-to-service");
  }

  @Bean
  public Retryer mailRetryer(MailRetryConfig retryConfig) {
    long backoffMillis = retryConfig.backoff().toMillis();
    return new Retryer.Default(backoffMillis, backoffMillis, retryConfig.maxRetries() + 1);
  }

  @Bean
  public ErrorDecoder mailErrorDecoder() {
    return new MailRetryErrorDecoder();
  }

}
