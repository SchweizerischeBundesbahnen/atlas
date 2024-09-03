package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ServicePointUpdateCsvModelTest {

  @Test
  void shouldBeValidServicePointUpdateModel() {
    ServicePointUpdateCsvModel bern = ServicePointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .build();
    assertThat(bern.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorsInServicePointUpdateModel() {
    ServicePointUpdateCsvModel bern = ServicePointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000")
        .number(8507000)
        .build();
    assertThat(bern.validate()).hasSize(3);
  }

  @Test
  void shouldReportInvalidServicePointNumber() {
    ServicePointUpdateCsvModel bern = ServicePointUpdateCsvModel.builder()
        .number(49685)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .build();
    assertThat(bern.validate()).hasSize(1);
  }
}