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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class RouteRewriteIntegrationTest {

  private static final int LINE_DIRECTORY_PORT = 9082;
  private static final int TIMETABLE_HEARING_PORT = 9095;
  private static final int WORKFLOW_PORT = 9096;
  private static final long BACKEND_REQUEST_TIMEOUT_MILLIS = 200;

  private static MockWebServer lineDirectoryMockServer;
  private static MockWebServer timetableHearingMockServer;
  private static MockWebServer workflowMockServer;

  @LocalServerPort
  private int port;

  private WebTestClient webClient;

  @BeforeAll
  static void setupServers() throws IOException {
    lineDirectoryMockServer = new MockWebServer();
    lineDirectoryMockServer.start(LINE_DIRECTORY_PORT);

    timetableHearingMockServer = new MockWebServer();
    timetableHearingMockServer.start(TIMETABLE_HEARING_PORT);

    workflowMockServer = new MockWebServer();
    workflowMockServer.start(WORKFLOW_PORT);
  }

  @AfterAll
  static void tearDownServers() throws IOException {
    lineDirectoryMockServer.shutdown();
    timetableHearingMockServer.shutdown();
    workflowMockServer.shutdown();
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

  @Test
  void shouldRouteLegacyTthV2PathToTimetableHearingService() throws InterruptedException {
    timetableHearingMockServer.enqueue(new MockResponse().setResponseCode(200));

    webClient.get().uri("/line-directory/v2/timetable-hearing/statements/external").exchange().expectStatus().isOk();

    RecordedRequest timetableHearingRequest = timetableHearingMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS,
        TimeUnit.MILLISECONDS);
    assertThat(timetableHearingRequest).isNotNull();
    assertThat(timetableHearingRequest.getPath()).isEqualTo("/v2/timetable-hearing/statements/external");

    assertThat(lineDirectoryMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)).isNull();
  }

  @Test
  void shouldRouteInternalTimetableHearingPathToLineDirectory() throws InterruptedException {
    lineDirectoryMockServer.enqueue(new MockResponse().setResponseCode(200));

    webClient.get().uri("/line-directory/internal/timetable-hearing/hearings").exchange().expectStatus().isOk();

    RecordedRequest lineDirectoryRequest = lineDirectoryMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS,
        TimeUnit.MILLISECONDS);
    assertThat(lineDirectoryRequest).isNotNull();
    assertThat(lineDirectoryRequest.getPath()).isEqualTo("/internal/timetable-hearing/hearings");

    assertThat(timetableHearingMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)).isNull();
  }

  @Test
  void shouldRouteWorkflowTthPathToWorkflow() throws InterruptedException {
    workflowMockServer.enqueue(new MockResponse().setResponseCode(200));

    webClient.get().uri("/workflow/internal/tth/dossier/5").exchange().expectStatus().isOk();

    RecordedRequest workflowRequest = workflowMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    assertThat(workflowRequest).isNotNull();
    assertThat(workflowRequest.getPath()).isEqualTo("/internal/tth/dossier/5");

    assertThat(timetableHearingMockServer.takeRequest(BACKEND_REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)).isNull();
  }
}


