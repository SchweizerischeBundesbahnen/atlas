package ch.sbb.prm.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.prm.directory.service.bulk.PlatformBulkImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlatformBulkImportControllerTest {

  @Mock
  private PlatformBulkImportService platformBulkImportService;

  private PlatformBulkImportController platformBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    platformBulkImportController = new PlatformBulkImportController(platformBulkImportService);
  }

  @Test
  void shouldDoBulkImportViaService() {
    BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid("ch:1:sloid:12345:1")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        platformBulkImportController.bulkImportPlatformReducedUpdate(List.of(updateContainer));

    verify(platformBulkImportService, never()).updatePlatformReducedByUsername("username", updateContainer);
    verify(platformBulkImportService).updatePlatformReduced(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkUpdateViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid("ch:1:sloid:12345:1")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        platformBulkImportController.bulkImportPlatformReducedUpdate(List.of(updateContainer));

    verify(platformBulkImportService).updatePlatformReducedByUsername(username, updateContainer);
    verify(platformBulkImportService, never()).updatePlatformReduced(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldReturnExecutionResultWithErrorResponse() {
    doThrow(new SloidNotFoundException("ch:1:sloid:12345:1")).when(platformBulkImportService).updatePlatformReduced(any());
    BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid("ch:1:sloid:12345:1")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        platformBulkImportController.bulkImportPlatformReducedUpdate(List.of(updateContainer));

    verify(platformBulkImportService).updatePlatformReduced(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(false);
  }

}
