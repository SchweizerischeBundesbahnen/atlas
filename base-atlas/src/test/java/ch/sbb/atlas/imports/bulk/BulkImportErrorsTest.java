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

  @Test
  void shouldBuildBulkImportErrorForDuplicatedSloid() {
    BulkImportError bulkImportError = BulkImportErrors.duplicatedValue("sloid", "ch:sloid:1");
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("sloid with value ch:sloid:1 occurred more than once");
    assertThat(bulkImportError.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.DUPLICATE_SLOID");
  }

  @Test
  void shouldBuildBulkImportErrorForDuplicatedNumber() {
    BulkImportError bulkImportError = BulkImportErrors.duplicatedValue("number", "8507000");
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("number with value 8507000 occurred more than once");
    assertThat(bulkImportError.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.DUPLICATE_NUMBER");
  }
}