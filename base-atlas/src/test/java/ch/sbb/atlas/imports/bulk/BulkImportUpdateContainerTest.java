package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import org.junit.jupiter.api.Test;

class BulkImportUpdateContainerTest {

  @Test
  void shouldEvaluateDataValidationErrorFalse() {
    BulkImportUpdateContainer<?> bulkImportUpdateContainer = BulkImportUpdateContainer.builder().lineNumber(1).build();
    assertThat(bulkImportUpdateContainer.hasDataValidationErrors()).isFalse();
  }

  @Test
  void shouldEvaluateDataValidationErrorTrue() {
    BulkImportUpdateContainer<?> bulkImportUpdateContainer =
        BulkImportUpdateContainer.builder().lineNumber(1).bulkImportLogEntry(BulkImportLogEntry.builder()
            .status(BulkImportStatus.DATA_VALIDATION_ERROR)
            .build()).build();
    assertThat(bulkImportUpdateContainer.hasDataValidationErrors()).isTrue();
  }
}