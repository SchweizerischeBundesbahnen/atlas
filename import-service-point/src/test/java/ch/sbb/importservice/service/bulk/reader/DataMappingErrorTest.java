package ch.sbb.importservice.service.bulk.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.importservice.service.bulk.reader.DataMappingError.ExpectedType;
import org.junit.jupiter.api.Test;

class DataMappingErrorTest {

  @Test
  void shouldBuildDataMappingError() {
    DataMappingError dataMappingError = DataMappingError.builder()
        .field("number")
        .errorValue("ch:sloid:1")
        .expectedType(ExpectedType.INTEGER)
        .build();
    assertThat(dataMappingError.getExpectedType()).isEqualTo(ExpectedType.INTEGER);
  }

  @Test
  void shouldMapToBulkImportError() {
    BulkImportError error = DataMappingError.builder()
        .field("number")
        .errorValue("ch:sloid:1")
        .expectedType(ExpectedType.INTEGER)
        .build().toBulkImportError();

    assertThat(error.getErrorMessage()).isEqualTo("Expected INTEGER but got ch:sloid:1 in column number");
    assertThat(error.getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.VALIDATION.DATA_MAPPING_ERROR");
    assertThat(error.getDisplayInfo().getParameters()).hasSize(3);
  }
}