package ch.sbb.atlas.imports.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.Validatable.UniqueField;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicePointGlobalIdUpdateCsvModelTest {

  @Test
  void shouldAcceptNumberAlone() {
    // A quarter of the foreign service points carry no sloid, so a number-only line has to be valid.
    List<BulkImportError> errors = ServicePointGlobalIdUpdateCsvModel.builder()
        .number(1105770)
        .globalId("de:05770:1282")
        .build()
        .validate();

    assertThat(errors).isEmpty();
  }

  @Test
  void shouldAcceptSloidAlone() {
    List<BulkImportError> errors = ServicePointGlobalIdUpdateCsvModel.builder()
        .sloid("ch:1:sloid:5770")
        .globalId("de:05770:1282")
        .build()
        .validate();

    assertThat(errors).isEmpty();
  }

  @Test
  void shouldRejectLineWithoutAnyIdentifier() {
    List<BulkImportError> errors = ServicePointGlobalIdUpdateCsvModel.builder()
        .globalId("de:05770:1282")
        .build()
        .validate();

    assertThat(errors).extracting(error -> error.getDisplayInfo().getCode())
        .contains("BULK_IMPORT.VALIDATION.SLOID_OR_NUMBER");
  }

  @Test
  void shouldTreatABlankSloidAsNoSloid() {
    List<BulkImportError> errors = ServicePointGlobalIdUpdateCsvModel.builder()
        .sloid("  ")
        .globalId("de:05770:1282")
        .build()
        .validate();

    assertThat(errors).extracting(error -> error.getDisplayInfo().getCode())
        .contains("BULK_IMPORT.VALIDATION.SLOID_OR_NUMBER");
  }

  @Test
  void shouldRejectAServicePointNumberThatIsNotSevenDigits() {
    List<BulkImportError> errors = ServicePointGlobalIdUpdateCsvModel.builder()
        .number(5770)
        .globalId("de:05770:1282")
        .build()
        .validate();

    assertThat(errors).isNotEmpty();
  }

  @Test
  void shouldDeclareAllThreeColumnsUnique() {
    // ATLAS fails every line of a duplicated group: an in-file duplicate is ambiguous and no repoint can resolve it.
    assertThat(ServicePointGlobalIdUpdateCsvModel.builder().build().uniqueFields())
        .extracting(UniqueField::getField)
        .containsExactly("sloid", "number", "globalId");
  }
}
