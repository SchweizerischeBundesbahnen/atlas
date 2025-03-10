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

class ExportServiceBatchSchedulerServiceTest {

  private static final Response RESPONSE = Response.builder().status(200).reason("OK")
      .request(Request.create(HttpMethod.POST, "/api", Collections.emptyMap(), null, Util.UTF_8, null)).build();

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
    when(client.exportServicePointBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportServicePointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldPostTriggerExportTrafficPointBatchSuccessfully() {
    //given
    when(client.exportTrafficPointBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportTrafficPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldPostTriggerExportLoadingPointBatchSuccessfully() {
    //given
    when(client.exportLoadingPointBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportLoadingPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldPostTriggerExportStopPointBatchSuccessfully() {
    //given
    when(client.exportStopPointBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportStopPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldPostTriggerExportPlatformBatchSuccessfully() {
    //given
    when(client.exportPlatformBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportPlatformBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportReferencePointBatchSuccessfully() {
    //given
    when(client.exportReferencePointBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportReferencePointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportContactPointBatchSuccessfully() {
    //given
    when(client.exportContactPointBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportContactPointBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportToiletBatchSuccessfully() {
    //given
    when(client.exportToiletBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportToiletBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldPostLoadCompaniesFromCRDUnsuccessful() {
    //given
    when(client.exportServicePointBatch()).thenReturn(RESPONSE.toBuilder().status(400).build());

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> exportServiceBatchSchedulerService.postTriggerExportServicePointBatch());
  }

  @Test
  void shouldTriggerExportParkingLotBatchSuccessfully() {
    //given
    when(client.exportParkingLotBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportParkingLotBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportRelationBatchSuccessfully() {
    //given
    when(client.exportRelationBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportRelationBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportBusinessOrganisationBatchSuccessfully() {
    //given
    when(client.exportBusinessOrganisationBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportBusinessOrganisationBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportTransportCompanyBatchSuccessfully() {
    //given
    when(client.exportTransportCompanyBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportTransportCompanyBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportLineBatchSuccessfully() {
    //given
    when(client.exportLineBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportLineBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportSublineBatchSuccessfully() {
    //given
    when(client.exportSublineBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportSublineBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldTriggerExportTimetableFieldNumberBatchSuccessfully() {
    //given
    when(client.exportTimetableFieldNumberBatch()).thenReturn(RESPONSE);

    //when
    Response result = exportServiceBatchSchedulerService.postTriggerExportTimetableFieldNumberBatch();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

}
