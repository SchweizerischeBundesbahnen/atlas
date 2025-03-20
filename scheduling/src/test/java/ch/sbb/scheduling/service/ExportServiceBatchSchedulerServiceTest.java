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
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class ExportServiceBatchSchedulerServiceTest {

  private static final Response RESPONSE = Response.builder()
      .status(200)
      .reason("OK")
      .request(Request.create(HttpMethod.POST, "/api", Collections.emptyMap(), null, Util.UTF_8, null))
      .build();

  private static Stream<BatchTestCase> batchTestCasesProvider() {
    final ExportServiceBatchClient client = Mockito.mock(ExportServiceBatchClient.class);
    return batchTestCases(client);
  }

  private static Stream<BatchTestCase> batchTestCases(ExportServiceBatchClient client) {
    final ExportServiceBatchSchedulerService schedulerService = new ExportServiceBatchSchedulerService(client);
    return Stream.of(
        new BatchTestCase(client::exportServicePointBatch, schedulerService::postTriggerExportServicePointBatch),
        new BatchTestCase(client::exportTrafficPointBatch, schedulerService::postTriggerExportTrafficPointBatch),
        new BatchTestCase(client::exportLoadingPointBatch, schedulerService::postTriggerExportLoadingPointBatch),
        new BatchTestCase(client::exportStopPointBatch, schedulerService::postTriggerExportStopPointBatch),
        new BatchTestCase(client::exportPlatformBatch, schedulerService::postTriggerExportPlatformBatch),
        new BatchTestCase(client::exportReferencePointBatch, schedulerService::postTriggerExportReferencePointBatch),
        new BatchTestCase(client::exportContactPointBatch, schedulerService::postTriggerExportContactPointBatch),
        new BatchTestCase(client::exportToiletBatch, schedulerService::postTriggerExportToiletBatch),
        new BatchTestCase(client::exportParkingLotBatch, schedulerService::postTriggerExportParkingLotBatch),
        new BatchTestCase(client::exportRelationBatch, schedulerService::postTriggerExportRelationBatch),
        new BatchTestCase(client::exportBusinessOrganisationBatch, schedulerService::postTriggerExportBusinessOrganisationBatch),
        new BatchTestCase(client::exportTransportCompanyBatch, schedulerService::postTriggerExportTransportCompanyBatch),
        new BatchTestCase(client::exportLineBatch, schedulerService::postTriggerExportLineBatch),
        new BatchTestCase(client::exportSublineBatch, schedulerService::postTriggerExportSublineBatch),
        new BatchTestCase(client::exportTimetableFieldNumberBatch, schedulerService::postTriggerExportTimetableFieldNumberBatch)
    );
  }

  private record BatchTestCase(Supplier<Response> clientFunction, Supplier<Response> serviceFunction) {

  }

  @ParameterizedTest
  @MethodSource("batchTestCasesProvider")
  void shouldPostTriggerExportBatchSuccessfully(BatchTestCase testCase) {
    //given
    when(testCase.clientFunction.get()).thenReturn(RESPONSE);

    //when
    Response result = testCase.serviceFunction.get();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldPostLoadCompaniesFromCRDUnsuccessful() {
    //given
    final ExportServiceBatchClient mock = Mockito.mock(ExportServiceBatchClient.class);
    when(mock.exportServicePointBatch()).thenReturn(RESPONSE.toBuilder().status(400).build());

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> new ExportServiceBatchSchedulerService(mock).postTriggerExportServicePointBatch());
  }

}
