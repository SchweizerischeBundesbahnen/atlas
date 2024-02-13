package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.ImportServicePointBatchClient;
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

 class ImportBatchSchedulerServiceTest {

  private ImportBatchSchedulerService importBatchSchedulerService;

  @Mock
  private ImportServicePointBatchClient client;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    importBatchSchedulerService = new ImportBatchSchedulerService(client);
  }

  @Test
   void shouldTriggerImportServicePointBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.triggerImportServicePointBatch()).thenReturn(response);

    //when
    Response result = importBatchSchedulerService.triggerImportServicePointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldTriggerImportTrafficPointBatchSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.triggerImportTrafficPointBatch()).thenReturn(response);

    //when
    Response result = importBatchSchedulerService.triggerImportTrafficPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
   void shouldTriggerImportServicePointBatchUnsuccessfully() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.triggerImportServicePointBatch()).thenReturn(response);

    //when & then
    assertThrows(SchedulingExecutionException.class,
        () -> importBatchSchedulerService.triggerImportServicePointBatch().close());
  }

  @Test
   void shouldTriggerImportTrafficPointBatchUnsuccessfully() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.triggerImportTrafficPointBatch()).thenReturn(response);

    //when & then
    assertThrows(SchedulingExecutionException.class,
        () -> importBatchSchedulerService.triggerImportTrafficPointBatch().close());
  }

  @Test
  void shouldTriggerImportLoadingPointBatchSuccessfully() {
   //given
   Response response = Response.builder()
           .status(200)
           .reason("OK")
           .request(
                   Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                           null, Util.UTF_8, null))
           .build();
   when(client.triggerImportLoadingPointBatch()).thenReturn(response);

   //when
   Response result = importBatchSchedulerService.triggerImportLoadingPointBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerImportStopPointBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.triggerImportStopPointBatch()).thenReturn(response);

   //when
   Response result = importBatchSchedulerService.triggerImportStopPointBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerImportPlatformBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.triggerImportPlatformBatch()).thenReturn(response);

   //when
   Response result = importBatchSchedulerService.triggerImportPlatformBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerImportReferencePointBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.triggerImportReferencePointBatch()).thenReturn(response);

   //when
   Response result = importBatchSchedulerService.triggerImportReferencePointBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerImportToiletBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.triggerImportToiletBatch()).thenReturn(response);

   //when
   Response result = importBatchSchedulerService.triggerImportToiletBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerImportParkingLotBatchSuccessfully() {
   //given
   Response response = Response.builder()
       .status(200)
       .reason("OK")
       .request(
           Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
               null, Util.UTF_8, null))
       .build();
   when(client.triggerImportParkingLotBatch()).thenReturn(response);

   //when
   Response result = importBatchSchedulerService.triggerImportParkingLotBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

}
