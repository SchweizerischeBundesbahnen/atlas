package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.LiDiClient;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import feign.Util;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class LidiSchedulerServiceTest {

  private LidiSchedulerService lidiSchedulerService;

  @Mock
  private LiDiClient liDiClient;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    lidiSchedulerService = new LidiSchedulerService(liDiClient);
  }

  @Test
  public void shouldExportFullLineVersionsCsvSuccessfully() {
    //given
    Response response = Response.builder()
                                .status(200)
                                .reason("OK")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportFullCsv()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullLineVersionsCsv();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  public void shouldExportFullLineVersionsCsvUnsuccessful() {
    //given
    Response response = Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .reason("Bad Request")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportFullCsv()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullLineVersionsCsv();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(400);
  }

  @Test
  public void shouldExportFullLineVersionsZipSuccessfully() {
    //given
    Response response = Response.builder()
                                .status(200)
                                .reason("OK")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportFullZip()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullLineVersionsZip();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  public void shouldExportFullLineVersionsZipUnsuccessful() {
    //given
    Response response = Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .reason("Bad Request")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportFullZip()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullLineVersionsZip();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(400);
  }

  @Test
  public void shouldExportActualLineVersionsCsvSuccessfully() {
    //given
    Response response = Response.builder()
                                .status(200)
                                .reason("OK")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportActualCsv()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualLineVersionsCsv();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  public void shouldExportActualLineVersionsCsvUnsuccessful() {
    //given
    Response response = Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .reason("Bad Request")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportActualCsv()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualLineVersionsCsv();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(400);
  }

  @Test
  public void shouldExportActualLineVersionsZipSuccessfully() {
    //given
    Response response = Response.builder()
                                .status(200)
                                .reason("OK")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportActualZip()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualLineVersionsZip();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  public void shouldExportActualLineVersionsZipUnsuccessful() {
    //given
    Response response = Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .reason("Bad Request")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportActualZip()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualLineVersionsZip();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(400);
  }

  @Test
  public void shouldNextTimetableLineVersionsCsvSuccessfully() {
    //given
    Response response = Response.builder()
                                .status(200)
                                .reason("OK")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportNextTimetableVersionsCsv()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableLineVersionsCsv();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  public void shouldExporttNextTimetableLineVersionsCsvUnsuccessful() {
    //given
    Response response = Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .reason("Bad Request")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportNextTimetableVersionsCsv()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableLineVersionsCsv();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(400);
  }

  @Test
  public void shouldExportNextTimetableLineVersionsZipSuccessfully() {
    //given
    Response response = Response.builder()
                                .status(200)
                                .reason("OK")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportNextTimetableVersionsZip()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableLineVersionsZip();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  public void shouldExportNextTimetableLineVersionsZipUnsuccessful() {
    //given
    Response response = Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .reason("Bad Request")
                                .request(
                                    Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                                        null, Util.UTF_8, null))
                                .build();
    when(liDiClient.putLiDiExportNextTimetableVersionsZip()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableLineVersionsZip();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(400);
  }

}