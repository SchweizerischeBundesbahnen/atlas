package ch.sbb.line.directory.module.bulkimport.subline.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.line.directory.module.bulkimport.subline.service.SublineBulkImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SublineBulkImportControllerInternalTest {

  @Mock
  private SublineBulkImportService sublineBulkImportService;

  private SublineBulkImportControllerInternal sublineBulkImportControllerInternal;

  @BeforeEach
  void setUp() {
    sublineBulkImportControllerInternal = new SublineBulkImportControllerInternal(sublineBulkImportService);
  }

  @Test
  void shouldDoBulkImportViaService() {
    BulkImportUpdateContainer<SublineUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
            .object(SublineUpdateCsvModel.builder()
                .slnid("ch:1:slnid:12345:1")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sublineBulkImportControllerInternal.sublineUpdate(List.of(updateContainer));

    verify(sublineBulkImportService, never()).updateSublineByUsername("username", updateContainer);
    verify(sublineBulkImportService).updateSubline(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkUpdateViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<SublineUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
            .object(SublineUpdateCsvModel.builder()
                .slnid("ch:1:slnid:12345")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sublineBulkImportControllerInternal.sublineUpdate(List.of(updateContainer));

    verify(sublineBulkImportService).updateSublineByUsername(username, updateContainer);
    verify(sublineBulkImportService, never()).updateSubline(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }
}