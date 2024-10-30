package ch.sbb.atlas.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class AtlasApiAuthGatewayApplicationTest {

  private static MockWebServer mockAtlasGateway;

  @MockBean
  private TokenService tokenService;

  @LocalServerPort
  private int port;

  private WebTestClient webClient;

  @BeforeAll
  static void setupServer() throws IOException {
    mockAtlasGateway = new MockWebServer();
    mockAtlasGateway.start(8888);
  }

  @AfterAll
  static void tearDownServer() throws IOException {
    mockAtlasGateway.shutdown();
  }

  @BeforeEach
  public void setup() {
    String baseUri = "http://localhost:" + port;
    webClient = WebTestClient.bindToServer().responseTimeout(Duration.ofSeconds(10)).baseUrl(baseUri).build();

    when(tokenService.getClientCredentialAccessToken()).thenReturn(Mono.just("access_token"));

    MockResponse response = new MockResponse().setResponseCode(200);
    mockAtlasGateway.enqueue(response);
  }

  @Test
  void applicationContextLoads() {
    assertThatNoException().isThrownBy(() -> {
    });
  }

  @Test
  void shouldGetLinesByGettingToken() throws InterruptedException {
    webClient.get().uri("/line-directory/v1/lines").exchange().expectStatus().isOk();

    RecordedRequest request = mockAtlasGateway.takeRequest();
    assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer access_token");
  }

  @Test
  void shouldGetLinesWithPassedToken() throws InterruptedException {
    webClient.get().uri("/line-directory/v1/lines").header(HttpHeaders.AUTHORIZATION, "Bearer passed_token")
        .exchange().expectStatus().isOk();

    RecordedRequest request = mockAtlasGateway.takeRequest();
    assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer passed_token");
  }
}