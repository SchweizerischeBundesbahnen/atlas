package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.ExportServiceBatchClient;
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

 class ExportServiceBatchSchedulerServiceTest {

  private ExportServiceBatchSchedulerService exportServiceBatchSchedulerService;

  @Mock
  private ExportServiceBatchClient client;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    exportServiceBatchSchedulerService = new ExportServiceBatchSchedulerService(client);
  }

  @Test
   void shouldPostTriggerExportServicePointBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.postTriggerExportServicePointBatch()).thenReturn(response);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportServicePointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldPostTriggerExportTrafficPointBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.postTriggerExportTrafficPointBatch()).thenReturn(response);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportTrafficPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldPostTriggerExportLoadingPointBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.postTriggerExportLoadingPointBatch()).thenReturn(response);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportLoadingPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldPostTriggerExportStopPointBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.postTriggerExportStopPointBatch()).thenReturn(response);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportStopPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldPostTriggerExportPlatformBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.postTriggerExportPlatformBatch()).thenReturn(response);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportPlatformBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportReferencePointBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.postTriggerExportReferencePointBatch()).thenReturn(response);

   //when
   Response result = exportServiceBatchSchedulerService.postTriggerExportReferencePointBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportContactPointBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.postTriggerExportContactPointBatch()).thenReturn(response);

   //when
   Response result = exportServiceBatchSchedulerService.postTriggerExportContactPointBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportToiletBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.postTriggerExportToiletBatch()).thenReturn(response);

   //when
   Response result = exportServiceBatchSchedulerService.postTriggerExportToiletBatch();

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
    when(client.postTriggerExportServicePointBatch()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class, () -> {
      exportServiceBatchSchedulerService.postTriggerExportServicePointBatch();
    });
  }

  @Test
  void shouldTriggerExportParkingLotBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.postTriggerExportParkingLotBatch()).thenReturn(response);

   //when
   Response result = exportServiceBatchSchedulerService.postTriggerExportParkingLotBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportRelationBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.postTriggerExportRelationBatch()).thenReturn(response);

   //when
   Response result = exportServiceBatchSchedulerService.postTriggerExportRelationBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportTransportCompanyBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.postTriggerExportTransportCompanyBatch()).thenReturn(response);

   //when
   Response result = exportServiceBatchSchedulerService.postTriggerExportTransportCompanyBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

}
