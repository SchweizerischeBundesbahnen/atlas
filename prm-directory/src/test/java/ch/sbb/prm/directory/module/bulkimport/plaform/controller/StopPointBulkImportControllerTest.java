package ch.sbb.prm.directory.module.bulkimport.plaform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.prm.directory.module.bulkimport.controller.StopPointBulkImportController;
import ch.sbb.prm.directory.module.bulkimport.service.StopPointBulkImportService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPointBulkImportControllerTest {

  @Mock
  private StopPointBulkImportService stopPointBulkImportService;

  private StopPointBulkImportController stopPointBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    stopPointBulkImportController = new StopPointBulkImportController(stopPointBulkImportService);
  }

  @Test
  void shouldBulkImportTerminate() {
    BulkImportUpdateContainer<SloidTerminateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
            .object(SloidTerminateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .validTo(LocalDate.of(2099, 12, 31))
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        stopPointBulkImportController.bulkImportStopPointTerminate(List.of(updateContainer));

    verify(stopPointBulkImportService, never()).terminateStopPointByUsername("username", updateContainer);
    verify(stopPointBulkImportService).terminateStopPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldBulkImportTerminateWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<SloidTerminateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
            .object(SloidTerminateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123")
                .validTo(LocalDate.of(2099, 12, 31))
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        stopPointBulkImportController.bulkImportStopPointTerminate(List.of(updateContainer));

    verify(stopPointBulkImportService).terminateStopPointByUsername(username, updateContainer);
    verify(stopPointBulkImportService, never()).terminateStopPoint(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

}
