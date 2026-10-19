package ch.sbb.atlas.api.mail;

import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("atlas.client.mail.retry")
public record MailRetryConfig(
    @DefaultValue("5") int maxRetries,
    @DefaultValue("5s") Duration backoff) {

  public MailRetryConfig {
    if (maxRetries < 0) {
      throw new IllegalArgumentException("maxRetries must not be negative");
    }
    if (maxRetries == Integer.MAX_VALUE) {
      throw new IllegalArgumentException("maxRetries must be less than " + Integer.MAX_VALUE);
    }
    Objects.requireNonNull(backoff, "backoff must not be null");
    if (backoff.isNegative()) {
      throw new IllegalArgumentException("backoff must not be negative");
    }
  }

}
