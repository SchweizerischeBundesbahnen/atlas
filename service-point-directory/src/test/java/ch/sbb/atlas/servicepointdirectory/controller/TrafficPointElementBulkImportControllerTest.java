package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk.TrafficPointElementBulkImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TrafficPointElementBulkImportControllerTest {

  @Mock
  private TrafficPointElementBulkImportService trafficPointElementBulkImportService;

  private TrafficPointElementBulkImportController trafficPointElementBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    trafficPointElementBulkImportController = new TrafficPointElementBulkImportController(trafficPointElementBulkImportService);
  }

  @Test
  void shouldDoBulkImportViaService() {
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        trafficPointElementBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(trafficPointElementBulkImportService, never()).updateTrafficPointByUserName("username", updateContainer);
    verify(trafficPointElementBulkImportService).updateTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkUpdateViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        trafficPointElementBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(trafficPointElementBulkImportService).updateTrafficPointByUserName(username, updateContainer);
    verify(trafficPointElementBulkImportService, never()).updateTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldReturnExecutionResultWithErrorResponse() {
    doThrow(new SloidNotFoundException("ch:1:sloid:89008:123:123")).when(trafficPointElementBulkImportService).updateTrafficPoint(any());
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        trafficPointElementBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(trafficPointElementBulkImportService).updateTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(false);
  }

  @Test
  void shouldBulkImportCreate() {
    BulkImportUpdateContainer<TrafficPointCreateCsvModel> updateContainer =
        BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
            .object(TrafficPointCreateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        trafficPointElementBulkImportController.bulkImportCreate(List.of(updateContainer));

    verify(trafficPointElementBulkImportService, never()).createTrafficPointByUserName("username", updateContainer);
    verify(trafficPointElementBulkImportService).createTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldBulkImportCreateWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<TrafficPointCreateCsvModel> updateContainer =
        BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
            .object(TrafficPointCreateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        trafficPointElementBulkImportController.bulkImportCreate(List.of(updateContainer));

    verify(trafficPointElementBulkImportService).createTrafficPointByUserName(username, updateContainer);
    verify(trafficPointElementBulkImportService, never()).createTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

}
