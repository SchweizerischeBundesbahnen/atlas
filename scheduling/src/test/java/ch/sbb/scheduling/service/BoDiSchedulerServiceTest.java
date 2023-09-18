package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.BoDiClient;
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

 class BoDiSchedulerServiceTest {

  private BoDiSchedulerService boDiSchedulerService;

  @Mock
  private BoDiClient boDiClient;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    boDiSchedulerService = new BoDiSchedulerService(boDiClient);
  }

  @Test
   void shouldPostLoadCompaniesFromCRDSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.postLoadCompaniesFromCRD()).thenReturn(response);

    //when
    Response result = boDiSchedulerService.postLoadCompaniesFromCRD();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldPostLoadCompaniesFromCRDUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.postLoadCompaniesFromCRD()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class, () -> {
      boDiSchedulerService.postLoadCompaniesFromCRD();
    });

  }

  @Test
   void shouldPostLoadCompaniesFromBAVSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.postLoadTransportCompaniesFromBav()).thenReturn(response);

    //when
    Response result = boDiSchedulerService.postLoadTransportCompaniesFromBav();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldPostLoadCompaniesFromBAVUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.postLoadTransportCompaniesFromBav()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> boDiSchedulerService.postLoadTransportCompaniesFromBav());

  }

  @Test
   void shouldExportFullBusinessOrganisationVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.putBoDiBusinessOrganisationExportFull()).thenReturn(response);

    //when
    Response result = boDiSchedulerService.exportFullBusinessOrganisationVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportFullBusinessOrganisationVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.putBoDiBusinessOrganisationExportFull()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> boDiSchedulerService.exportFullBusinessOrganisationVersions());
  }

  @Test
   void shouldExportActualBusinessOrganisationVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.putBoDiBusinessOrganisationExportActual()).thenReturn(response);

    //when
    Response result = boDiSchedulerService.exportActualBusinessOrganisationVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportActualBusinessOrganisationVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.putBoDiBusinessOrganisationExportActual()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> boDiSchedulerService.exportActualBusinessOrganisationVersions());
  }

  @Test
   void shouldNextTimetableBusinessOrganisationVersionsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.putBoDiBusinessOrganisationExportNextTimetableVersions()).thenReturn(response);

    //when
    Response result = boDiSchedulerService.exportNextTimetableBusinessOrganisationVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldExportNextTimetableBusinessOrganisationVersionsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(boDiClient.putBoDiBusinessOrganisationExportNextTimetableVersions()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> boDiSchedulerService.exportNextTimetableBusinessOrganisationVersions());
  }

}