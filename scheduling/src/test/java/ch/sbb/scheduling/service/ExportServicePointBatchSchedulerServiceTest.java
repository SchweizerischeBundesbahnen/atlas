package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.ExportServicePointBatchClient;
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

 class ExportServicePointBatchSchedulerServiceTest {

  private ExportServicePointBatchSchedulerService exportServicePointBatchSchedulerService;

  @Mock
  private ExportServicePointBatchClient client;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    exportServicePointBatchSchedulerService = new ExportServicePointBatchSchedulerService(client);
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
    Response result = exportServicePointBatchSchedulerService.postTriggerExportServicePointBatch();

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
    Response result = exportServicePointBatchSchedulerService.postTriggerExportTrafficPointBatch();

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
    Response result = exportServicePointBatchSchedulerService.postTriggerExportLoadingPointBatch();

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
    Response result = exportServicePointBatchSchedulerService.postTriggerExportStopPointBatch();

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
   Response result = exportServicePointBatchSchedulerService.postTriggerExportReferencePointBatch();

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
      exportServicePointBatchSchedulerService.postTriggerExportServicePointBatch();
    });

  }

}
