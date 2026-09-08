package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import java.util.List;
import org.junit.jupiter.api.Test;

class BulkImportErrorsTest {

  @Test
  void shouldBuildBulkImportErrorForSloidXorNumber() {
    BulkImportError bulkImportError = BulkImportErrors.sloidXorNumber();
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("SlOID xor number must be given");
  }

  @Test
  void shouldBuildBulkImportErrorForSloidOrNumber() {
    BulkImportError bulkImportError = BulkImportErrors.sloidOrNumber();
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("SlOID or number must be given");
    assertThat(bulkImportError.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.SLOID_OR_NUMBER");
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
    assertThat(bulkImportError.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.DUPLICATE_FIELD");
  }

  @Test
  void shouldBuildBulkImportErrorForDuplicatedNumber() {
    BulkImportError bulkImportError = BulkImportErrors.duplicatedValue("number", "8507000");
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("number with value 8507000 occurred more than once");
    assertThat(bulkImportError.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.DUPLICATE_FIELD");
  }

  @Test
  void shouldBuildBulkImportErrorForStopPointSloidXorNumber() {
    BulkImportError bulkImportError = BulkImportErrors.stopPointSloidXorNumber();
    assertThat(bulkImportError.getErrorMessage()).isEqualTo("stopPointSloid xor number must be given");
    assertThat(bulkImportError.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.STOPPOINTSLOID_XOR_NUMBER");
  }

  @Test
  void shouldBuildBulkImportErrorForNotNullFields() {
    SectorCreateCsvModel model = SectorCreateCsvModel.builder().build();

    List<BulkImportError> bulkImportErrors = BulkImportErrors.notNullForFields(model, List.of(SectorCreateCsvModel.Fields.trafficPointSloid));

    assertThat(bulkImportErrors).hasSize(1);
    assertThat(bulkImportErrors.getFirst().getErrorMessage()).isEqualTo("Field trafficPointSloid must not be null");
  }
}
