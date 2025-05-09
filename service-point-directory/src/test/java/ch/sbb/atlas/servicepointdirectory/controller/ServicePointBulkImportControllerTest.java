package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk.ServicePointBulkImportService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ServicePointBulkImportControllerTest {

  @Mock
  private ServicePointBulkImportService servicePointBulkImportService;

  private ServicePointBulkImportController servicePointBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    servicePointBulkImportController = new ServicePointBulkImportController(servicePointBulkImportService);
  }

  @Test
  void shouldBulkUpdateViaService() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .build())
            .build();
    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults = servicePointBulkImportController.bulkImportUpdate(
        List.of(updateContainer));

    verify(servicePointBulkImportService, never()).updateServicePointByUserName("userName", updateContainer);
    verify(servicePointBulkImportService).updateServicePoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first().extracting(BulkImportItemExecutionResult::isSuccess)
        .isEqualTo(true);
  }

  @Test
  void shouldBulkUpdateViaServiceWithUserName() {
    String userName = "e123456";
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .build())
            .inNameOf(userName)
            .build();
    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults = servicePointBulkImportController.bulkImportUpdate(
        List.of(updateContainer));

    verify(servicePointBulkImportService).updateServicePointByUserName(userName, updateContainer);
    verify(servicePointBulkImportService, never()).updateServicePoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first().extracting(BulkImportItemExecutionResult::isSuccess)
        .isEqualTo(true);
  }

  @Test
  void shouldBulkTerminateViaService() {
    BulkImportUpdateContainer<ServicePointTerminateCsvModel> updateContainer =
        BulkImportUpdateContainer.<ServicePointTerminateCsvModel>builder()
            .object(ServicePointTerminateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .number(8500001)
                .validTo(LocalDate.of(2020, 1, 1))
                .build())
            .build();
    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults = servicePointBulkImportController.bulkImportTerminate(
        List.of(updateContainer));

    verify(servicePointBulkImportService, never()).terminateServicePointByUserName("userName", updateContainer);
    verify(servicePointBulkImportService).terminateServicePoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first().extracting(BulkImportItemExecutionResult::isSuccess)
        .isEqualTo(true);
  }

  @Test
  void shouldBulkTerminateViaServiceWithUserName() {
    String userName = "e123456";
    BulkImportUpdateContainer<ServicePointTerminateCsvModel> updateContainer =
        BulkImportUpdateContainer.<ServicePointTerminateCsvModel>builder()
            .object(ServicePointTerminateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .number(8500001)
                .validTo(LocalDate.of(2020, 1, 1))
                .build())
            .inNameOf(userName)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults = servicePointBulkImportController.bulkImportTerminate(
        List.of(updateContainer));

    verify(servicePointBulkImportService).terminateServicePointByUserName(userName, updateContainer);
    verify(servicePointBulkImportService, never()).terminateServicePoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first().extracting(BulkImportItemExecutionResult::isSuccess)
        .isEqualTo(true);
  }

  @Test
  void shouldReturnExecutionResultWithErrorResponse() {
    doThrow(new SloidNotFoundException("ch:1:sloid:7000")).when(servicePointBulkImportService).updateServicePoint(any());

    BulkImportUpdateContainer<ServicePointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .build())
            .build();
    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults = servicePointBulkImportController.bulkImportUpdate(
        List.of(updateContainer));

    verify(servicePointBulkImportService).updateServicePoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first().extracting(BulkImportItemExecutionResult::isSuccess)
        .isEqualTo(false);
  }
}
