package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class BulkImportLogEntryTest {

  @Test
  void shouldBuildLogEntryCorrectly() {
    BulkImportLogEntry logEntry =
        BulkImportLogEntry.builder().lineNumber(1).status(BulkImportStatus.DATA_VALIDATION_ERROR).errors(List.of(
            BulkImportError.builder()
                .errorMessage("Data validation error")
                .displayInfo(DisplayInfo.builder().code("ERROR.DATA_VALIDATION").build())
                .build())).build();

    assertThat(logEntry).isNotNull();
  }
}