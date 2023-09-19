package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.LiDiClient;
import ch.sbb.scheduling.exception.SchedulingExecutionException;
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
   void setUp() {
    MockitoAnnotations.openMocks(this);
    lidiSchedulerService = new LidiSchedulerService(liDiClient);
  }

  @Test
   void shouldExportFullLineVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiLineExportFull()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullLineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportFullLineVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiLineExportFull()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportFullLineVersions());
  }

  @Test
   void shouldExportActualLineVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiLineExportActual()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualLineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportActualLineVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiLineExportActual()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportActualLineVersions());
  }

  @Test
   void shouldNextTimetableLineVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiLineExportNextTimetableVersions()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableLineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportNextTimetableLineVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiLineExportNextTimetableVersions()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportNextTimetableLineVersions());
  }

  @Test
   void shouldExportFullSublineVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiSublineExportFull()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullSublineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportFullSublineVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiSublineExportFull()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportFullSublineVersions());
  }

  @Test
   void shouldExportActualSublineVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiSublineExportActual()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualSublineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportActualSublineVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiSublineExportActual()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportActualSublineVersions());
  }

  @Test
   void shouldNextTimetableSublineVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiSublineExportNextTimetableVersions()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableSublineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExporttNextTimetableSublineVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiSublineExportNextTimetableVersions()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportNextTimetableSublineVersions());
  }

  @Test
   void shouldExportFullTimetableFieldNumberVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiTimetableFieldNumberExportFull()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportFullTimetableFieldNumberVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportFullTimetableFieldNumberVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiTimetableFieldNumberExportFull()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportFullTimetableFieldNumberVersions());
  }

  @Test
   void shouldExportActualTimetableFieldNumberVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiTimetableFieldNumberExportActual()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportActualTimetableFieldNumberVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportActualTimetableFieldNumberVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiTimetableFieldNumberExportActual()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportActualTimetableFieldNumberVersions());
  }

  @Test
   void shouldNextTimetableTimetableFieldNumberVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiTimetableFieldNumberExportNextTimetableVersions()).thenReturn(response);

    //when
    Response result = lidiSchedulerService.exportNextTimetableTimetableFieldNumberVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportNextTimetableTimetableFieldNumberVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(liDiClient.putLiDiTimetableFieldNumberExportNextTimetableVersions()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> lidiSchedulerService.exportNextTimetableTimetableFieldNumberVersions());
  }

}