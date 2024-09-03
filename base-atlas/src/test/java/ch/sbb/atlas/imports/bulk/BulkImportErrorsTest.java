package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import org.junit.jupiter.api.Test;

class BulkImportErrorsTest {

  @Test
  void shouldBuildBulkImportErrorForSloidXorNumber() {
    BulkImportError bulkImportError = BulkImportErrors.sloidXorNumber();
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("SlOID xor number must be given");
  }

  @Test
  void shouldBuildBulkImportErrorForNotNull() {
    BulkImportError bulkImportError = BulkImportErrors.notNull("validFrom");
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("Field validFrom must not be null");
  }

  @Test
  void shouldBuildBulkImportErrorForInvalidServicePointNumber() {
    BulkImportError bulkImportError = BulkImportErrors.invalidServicePointNumber();
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("Invalid Service Point Number");
  }
}