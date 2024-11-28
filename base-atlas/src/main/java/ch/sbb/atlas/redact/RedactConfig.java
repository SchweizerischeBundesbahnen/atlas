package ch.sbb.atlas.redact;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedactConfig {

  @Bean
  public RedactDecider redactDecider(Optional<RedactBySboidDecider> redactBySboidDecider) {
    return new RedactDecider(redactBySboidDecider);
  }

  @Bean
  public RedactAspect redactAspect(RedactDecider redactDecider) {
    return new RedactAspect(redactDecider);
  }

}
