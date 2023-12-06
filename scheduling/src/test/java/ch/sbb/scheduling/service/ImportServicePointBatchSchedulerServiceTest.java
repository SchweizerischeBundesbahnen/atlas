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

 class ImportServicePointBatchSchedulerServiceTest {

  private ImportServicePointBatchSchedulerService importServicePointBatchSchedulerService;

  @Mock
  private ImportServicePointBatchClient client;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    importServicePointBatchSchedulerService = new ImportServicePointBatchSchedulerService(client);
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
    Response result = importServicePointBatchSchedulerService.triggerImportServicePointBatch();

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
    Response result = importServicePointBatchSchedulerService.triggerImportTrafficPointBatch();

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
        () -> importServicePointBatchSchedulerService.triggerImportServicePointBatch().close());
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
        () -> importServicePointBatchSchedulerService.triggerImportTrafficPointBatch().close());
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
   Response result = importServicePointBatchSchedulerService.triggerImportStopPointBatch();

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
   Response result = importServicePointBatchSchedulerService.triggerImportPlatformBatch();

   //then
   assertThat(result).isNotNull();
   assertThat(result.status()).isEqualTo(200);
  }

}
