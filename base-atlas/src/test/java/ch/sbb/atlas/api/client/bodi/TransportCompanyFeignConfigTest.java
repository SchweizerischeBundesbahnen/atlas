package ch.sbb.atlas.api.client.bodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.annotation.RetryableFeignRequest;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

class TransportCompanyFeignConfigTest {

  private static final TransportCompanyModel TRANSPORT_COMPANY = TransportCompanyModel.builder().id(1L).build();

  private final Client httpClient = mock(Client.class);
  private final TransportCompanyFeignConfig config = new TransportCompanyFeignConfig();
  private final TransportCompanyClient client = createClient(TransportCompanyClient.class);

  @Test
  void shouldScopeRetryConfigurationToTransportCompanyClient() {
    new ApplicationContextRunner()
        .withUserConfiguration(FeignAutoConfiguration.class, ClientConfiguration.class)
        .withPropertyValues("atlas.client.gateway.url=http://localhost")
        .run(context -> {
          assertThat(context).doesNotHaveBean(Retryer.class).doesNotHaveBean(ErrorDecoder.class);
          FeignClientFactory factory = context.getBean(FeignClientFactory.class);
          assertThat(factory.getInstance("transportCompanyClient", Retryer.class))
              .isInstanceOf(TransportCompanyRetryer.class);
          assertThat(factory.getInstance("transportCompanyClient", ErrorDecoder.class))
              .isInstanceOf(TransportCompanyRetryErrorDecoder.class);
          assertThat(factory.getInstance("otherClient", Retryer.class)).isSameAs(Retryer.NEVER_RETRY);
        });
  }

  @ParameterizedTest
  @ValueSource(ints = {502, 503, 504})
  void shouldRecoverOnThirdAttemptForBothGetRequests(int status) throws IOException {
    when(httpClient.execute(any(), any()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), status, Map.of()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), status, Map.of()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 200, Map.of()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), status, Map.of()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), status, Map.of()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 200, Map.of()));

    long start = System.nanoTime();
    assertThat(client.getTransportCompany(1L)).isSameAs(TRANSPORT_COMPANY);
    assertThat(client.getTransportCompaniesBySboid("ch:1:sboid:100052")).containsExactly(TRANSPORT_COMPANY);

    assertThat(Duration.ofNanos(System.nanoTime() - start)).isGreaterThanOrEqualTo(Duration.ofSeconds(4));
    verify(httpClient, times(6)).execute(any(), any());
  }

  @Test
  void shouldPropagateFailureAfterTwoRetries() throws IOException {
    stubResponse(503, Map.of());

    assertThatThrownBy(() -> client.getTransportCompany(1L))
        .isInstanceOfSatisfying(RetryableException.class, exception -> {
          assertThat(exception.status()).isEqualTo(503);
          assertThat(exception.contentUTF8()).isEmpty();
          assertThat(exception.getCause()).isInstanceOfSatisfying(FeignException.class,
              cause -> assertThat(cause.contentUTF8()).isEqualTo("Failure"));
        });

    verify(httpClient, times(3)).execute(any(), any());
  }

  @Test
  void shouldRetryConnectionFailuresAndTimeouts() throws IOException {
    when(httpClient.execute(any(), any()))
        .thenThrow(new IOException("Connection reset"))
        .thenThrow(new SocketTimeoutException("Read timed out"))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 200, Map.of()));

    assertThat(client.getTransportCompany(1L)).isSameAs(TRANSPORT_COMPANY);

    verify(httpClient, times(3)).execute(any(), any());
  }

  @ParameterizedTest
  @ValueSource(ints = {400, 401, 403, 404, 408, 500, 501})
  void shouldNotRetryOtherHttpErrorsEvenWithRetryAfter(int status) throws IOException {
    stubResponse(status, Map.of("Retry-After", List.of("0")));

    assertThatThrownBy(() -> client.getTransportCompany(1L))
        .isInstanceOf(FeignException.class)
        .isNotInstanceOf(RetryableException.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldRetryRateLimitingWithShortRetryAfter() throws IOException {
    when(httpClient.execute(any(), any()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 429, Map.of("Retry-After", List.of("1"))))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 200, Map.of()));

    assertThat(client.getTransportCompany(1L)).isSameAs(TRANSPORT_COMPANY);

    verify(httpClient, times(2)).execute(any(), any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "invalid"})
  void shouldNotRetryRateLimitingWithoutValidRetryAfter(String retryAfter) throws IOException {
    stubResponse(429, retryAfter.isEmpty() ? Map.of() : Map.of("Retry-After", List.of(retryAfter)));

    assertThatThrownBy(() -> client.getTransportCompany(1L))
        .isInstanceOf(FeignException.TooManyRequests.class);

    verify(httpClient).execute(any(), any());
  }

  @ParameterizedTest
  @ValueSource(ints = {429, 503})
  void shouldNotRetryBeforeLongRetryAfterOrExceedBackoffBudget(int status) throws IOException {
    stubResponse(status, Map.of("Retry-After", List.of("60")));

    assertThatThrownBy(() -> client.getTransportCompany(1L)).isInstanceOf(RetryableException.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldRespectRetryAfterHttpDate() throws IOException {
    String retryAfter = ZonedDateTime.now().plusMinutes(1).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    stubResponse(503, Map.of("retry-after", List.of(retryAfter)));

    assertThatThrownBy(() -> client.getTransportCompany(1L)).isInstanceOf(RetryableException.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldNotRetryBavImportOnHttpFailure() throws IOException {
    stubResponse(503, Map.of("Retry-After", List.of("0")));

    assertThatThrownBy(client::loadTransportCompaniesFromBav)
        .isInstanceOf(FeignException.ServiceUnavailable.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldNotRetryBavImportOnConnectionFailure() throws IOException {
    when(httpClient.execute(any(), any())).thenThrow(new SocketTimeoutException("Read timed out"));

    assertThatThrownBy(client::loadTransportCompaniesFromBav).isInstanceOf(RetryableException.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldNotRetryNewGetEndpointOnHttpFailure() throws IOException {
    AdditionalEndpointClient additionalClient = createClient(AdditionalEndpointClient.class);
    stubResponse(503, Map.of("Retry-After", List.of("0")));

    assertThatThrownBy(additionalClient::searchTransportCompanies)
        .isInstanceOf(FeignException.ServiceUnavailable.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldNotRetryNewGetEndpointOnConnectionFailure() throws IOException {
    AdditionalEndpointClient additionalClient = createClient(AdditionalEndpointClient.class);
    when(httpClient.execute(any(), any())).thenThrow(new SocketTimeoutException("Read timed out"));

    assertThatThrownBy(additionalClient::searchTransportCompanies).isInstanceOf(RetryableException.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldRetryMarkedMethodAfterPathChange() throws IOException {
    AdditionalEndpointClient additionalClient = createClient(AdditionalEndpointClient.class);
    when(httpClient.execute(any(), any()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 503, Map.of()))
        .thenThrow(new SocketTimeoutException("Read timed out"))
        .thenAnswer(invocation -> response(invocation.getArgument(0), 200, Map.of()));

    assertThat(additionalClient.getTransportCompany(1L)).isSameAs(TRANSPORT_COMPANY);

    verify(httpClient, times(3)).execute(any(), any());
  }

  @Test
  void shouldNotRetryMarkedPostMethod() throws IOException {
    AdditionalEndpointClient additionalClient = createClient(AdditionalEndpointClient.class);
    when(httpClient.execute(any(), any())).thenThrow(new SocketTimeoutException("Read timed out"));

    assertThatThrownBy(() -> additionalClient.updateTransportCompany(1L)).isInstanceOf(RetryableException.class);

    verify(httpClient).execute(any(), any());
  }

  @Test
  void shouldStopRetryingWhenInterrupted() throws IOException {
    stubResponse(503, Map.of());
    Thread.currentThread().interrupt();
    try {
      assertThatThrownBy(() -> client.getTransportCompany(1L)).isInstanceOf(RetryableException.class);
      assertThat(Thread.currentThread().isInterrupted()).isTrue();
      verify(httpClient).execute(any(), any());
    } finally {
      Thread.interrupted();
    }
  }

  private <T> T createClient(Class<T> clientType) {
    return Feign.builder()
        .contract(new SpringMvcContract())
        .client(httpClient)
        .retryer(config.transportCompanyRetryer())
        .errorDecoder(config.transportCompanyErrorDecoder())
        .decoder((response, type) -> type == TransportCompanyModel.class ? TRANSPORT_COMPANY : List.of(TRANSPORT_COMPANY))
        .target(clientType, "http://localhost/business-organisation-directory");
  }

  private void stubResponse(int status, Map<String, Collection<String>> headers) throws IOException {
    when(httpClient.execute(any(), any()))
        .thenAnswer(invocation -> response(invocation.getArgument(0), status, headers));
  }

  private Response response(Request request, int status, Map<String, Collection<String>> headers) {
    return Response.builder()
        .status(status)
        .reason(status == 200 ? "OK" : "Failure")
        .request(request)
        .headers(headers)
        .body(status == 200 ? "" : "Failure", StandardCharsets.UTF_8)
        .build();
  }

  @EnableFeignClients(clients = TransportCompanyClient.class)
  static class ClientConfiguration {

  }

  interface AdditionalEndpointClient extends TransportCompanyClient {

    @Override
    @RetryableFeignRequest
    @GetMapping("/renamed-transport-companies/{id}")
    TransportCompanyModel getTransportCompany(@PathVariable("id") Long id);

    @GetMapping("/internal/transport-companies/search")
    List<TransportCompanyModel> searchTransportCompanies();

    @RetryableFeignRequest
    @PostMapping("/internal/transport-companies/{id}")
    void updateTransportCompany(@PathVariable("id") Long id);

  }

}
