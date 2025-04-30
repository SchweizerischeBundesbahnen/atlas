package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.line.directory.service.bulk.LineBulkImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineBulkImportControllerTest {

  @Mock
  private LineBulkImportService lineBulkImportService;

  private LineBulkImportController lineBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    lineBulkImportController = new LineBulkImportController(lineBulkImportService);
  }

  @Test
  void shouldDoBulkImportViaService() {
    BulkImportUpdateContainer<LineUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<LineUpdateCsvModel>builder()
            .object(LineUpdateCsvModel.builder()
                .slnid("ch:1:slnid:12345")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        lineBulkImportController.lineUpdate(List.of(updateContainer));

    verify(lineBulkImportService, never()).updateLineByUsername("username", updateContainer);
    verify(lineBulkImportService).updateLine(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkUpdateViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<LineUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<LineUpdateCsvModel>builder()
            .object(LineUpdateCsvModel.builder()
                .slnid("ch:1:slnid:12345")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        lineBulkImportController.lineUpdate(List.of(updateContainer));

    verify(lineBulkImportService).updateLineByUsername(username, updateContainer);
    verify(lineBulkImportService, never()).updateLine(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

}