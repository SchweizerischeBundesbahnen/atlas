package ch.sbb.atlas.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class RouteRewriteIntegrationTest {

  private static final int LINE_DIRECTORY_PORT = 9082;
  private static final int TIMETABLE_HEARING_PORT = 9095;
  private static final long BACKEND_REQUEST_TIMEOUT_MILLIS = 200;

  private static MockWebServer lineDirectoryMockServer;
  private static MockWebServer timetableHearingMockServer;

  @LocalServerPort
  private int port;

  private WebTestClient webClient;

  @BeforeAll
  static void setupServers() throws IOException {
    lineDirectoryMockServer = new MockWebServer();
    lineDirectoryMockServer.start(LINE_DIRECTORY_PORT);

    timetableHearingMockServer = new MockWebServer();
    timetableHearingMockServer.start(TIMETABLE_HEARING_PORT);
  }

  @AfterAll
  static void tearDownServers() throws IOException {
    lineDirectoryMockServer.shutdown();
    timetableHearingMockServer.shutdown();
  }

  @BeforeEach
  void setupClient() {
    String baseUri = "http://localhost:" + port;
    webClient = WebTestClient.bindToServer()
        .responseTimeout(Duration.ofSeconds(10))
        .baseUrl(baseUri)
        .build();
  }

  @Test
  void shouldRewritePathForLineDirectoryRoute() throws InterruptedException {
    lineDirectoryMockServer.enqueue(new MockResponse().setResponseCode(200));
    timetableHearingMockServer.enqueue(new MockResponse().setResponseCode(200));

    webClient.get().uri("/line-directory/v1/lines").exchange().expectStatus().isOk();

    RecordedRequest recordedRequest = lineDirectoryMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    assertThat(recordedRequest).isNotNull();
    assertThat(recordedRequest.getPath()).isEqualTo("/v1/lines");
  }

  @Nested
  @TestPropertySource(properties = "gateway.tthModuleReroute=true")
  class TthModuleRerouteEnabled {

    @Test
    void shouldRerouteTthCutoverToTimetableHearingService() throws InterruptedException {
      lineDirectoryMockServer.enqueue(new MockResponse().setResponseCode(200));
      timetableHearingMockServer.enqueue(new MockResponse().setResponseCode(200));

      webClient.get().uri("/line-directory/v1/timetable-hearing/hearings").exchange().expectStatus().isOk();

      RecordedRequest timetableHearingRequest = timetableHearingMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS,
          TimeUnit.MILLISECONDS);
      assertThat(timetableHearingRequest).isNotNull();
      assertThat(timetableHearingRequest.getPath()).isEqualTo("/v1/timetable-hearing/hearings");

      RecordedRequest lineDirectoryRequest = lineDirectoryMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS,
          TimeUnit.MILLISECONDS);
      assertThat(lineDirectoryRequest).isNull();
    }
  }

  @Nested
  @TestPropertySource(properties = "gateway.tthModuleReroute=false")
  class TthModuleRerouteDisabled {

    @Test
    void shouldNotRerouteTimetableHearingWhenTthModuleRerouteDisabled() throws InterruptedException {
      lineDirectoryMockServer.enqueue(new MockResponse().setResponseCode(200));
      timetableHearingMockServer.enqueue(new MockResponse().setResponseCode(200));

      webClient.get().uri("/line-directory/v1/timetable-hearing/hearings").exchange().expectStatus().isOk();

      RecordedRequest lineDirectoryRequest = lineDirectoryMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS,
          TimeUnit.MILLISECONDS);
      assertThat(lineDirectoryRequest).isNotNull();
      assertThat(lineDirectoryRequest.getPath()).isEqualTo("/v1/timetable-hearing/hearings");

      RecordedRequest timetableHearingRequest = timetableHearingMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS,
          TimeUnit.MILLISECONDS);
      assertThat(timetableHearingRequest).isNull();
    }
  }
}


