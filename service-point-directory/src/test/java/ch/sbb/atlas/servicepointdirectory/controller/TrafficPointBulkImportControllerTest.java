package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointBulkImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TrafficPointBulkImportControllerTest {

  @Mock
  private TrafficPointBulkImportService trafficPointBulkImportService;

  @Mock AtlasExceptionHandler atlasExceptionHandler;

  private TrafficPointBulkImportController trafficPointBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    when(atlasExceptionHandler.mapToErrorResponse(any()))
        .thenAnswer(i -> i.getArgument(0, AtlasException.class).getErrorResponse());
    trafficPointBulkImportController = new TrafficPointBulkImportController(trafficPointBulkImportService, atlasExceptionHandler);
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
        trafficPointBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(trafficPointBulkImportService, never()).updateTrafficPointByUserName("username", updateContainer);
    verify(trafficPointBulkImportService).updateTrafficPoint(updateContainer);
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
        trafficPointBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(trafficPointBulkImportService).updateTrafficPointByUserName(username, updateContainer);
    verify(trafficPointBulkImportService, never()).updateTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldReturnExecutionResultWithErrorResponse() {
    doThrow(new SloidNotFoundException("ch:1:sloid:89008:123:123")).when(trafficPointBulkImportService).updateTrafficPoint(any());
    BulkImportUpdateContainer<TrafficPointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        trafficPointBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(trafficPointBulkImportService).updateTrafficPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(false);
  }

}
