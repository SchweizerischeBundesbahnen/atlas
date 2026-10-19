package ch.sbb.atlas.api.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

class MailFeignConfigTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withUserConfiguration(MailFeignConfig.class)
      .withBean(ClientRegistrationRepository.class, () -> mock(ClientRegistrationRepository.class))
      .withBean(OAuth2AuthorizedClientService.class, () -> mock(OAuth2AuthorizedClientService.class));

  @Test
  void shouldUseDefaultMailRetryConfiguration() {
    contextRunner.run(context -> assertThat(context.getBean(MailRetryConfig.class))
        .isEqualTo(new MailRetryConfig(5, Duration.ofSeconds(5))));
  }

  @Test
  void shouldOverrideMailRetryConfiguration() {
    contextRunner
        .withPropertyValues(
            "atlas.client.mail.retry.max-retries=2",
            "atlas.client.mail.retry.backoff=100ms"
        )
        .run(context -> assertThat(context.getBean(MailRetryConfig.class))
            .isEqualTo(new MailRetryConfig(2, Duration.ofMillis(100))));
  }

  @Test
  void shouldAllowConfiguredNumberOfRetries() {
    Retryer retryer = new MailFeignConfig().mailRetryer(new MailRetryConfig(5, Duration.ZERO));
    RetryableException failure = mock(RetryableException.class);

    for (int retry = 0; retry < 5; retry++) {
      assertThatNoException().isThrownBy(() -> retryer.continueOrPropagate(failure));
    }
    assertThatThrownBy(() -> retryer.continueOrPropagate(failure))
        .isSameAs(failure);
  }

  @Test
  void shouldRetryTransientHttpErrors() {
    ErrorDecoder errorDecoder = new MailFeignConfig().mailErrorDecoder();

    assertThat(errorDecoder.decode("sendMail", response(500)))
        .isInstanceOf(RetryableException.class);
    assertThat(errorDecoder.decode("sendMail", response(429)))
        .isInstanceOf(RetryableException.class);
    assertThat(errorDecoder.decode("sendMail", response(408)))
        .isInstanceOf(RetryableException.class);
    assertThat(errorDecoder.decode("sendMail", response(400)))
        .isNotInstanceOf(RetryableException.class);
  }

  private Response response(int status) {
    Request request = Request.create(
        Request.HttpMethod.POST,
        "http://localhost/mail/internal/send",
        Map.of(),
        null,
        StandardCharsets.UTF_8,
        null
    );
    return Response.builder()
        .status(status)
        .reason("Failure")
        .request(request)
        .headers(Map.of())
        .build();
  }

}
