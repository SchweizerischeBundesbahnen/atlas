package ch.sbb.atlas.imports.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LineUpdateCsvModelTest {

  @Test
  void shouldBeValidLineUpdateCsvModel() {
    LineUpdateCsvModel lineUpdateCsvModel = LineUpdateCsvModel.builder()
        .slnid("ch:1:sloid:88253:0:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(lineUpdateCsvModel.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInLineUpdateCsvModel() {
    LineUpdateCsvModel lineUpdateCsvModel = LineUpdateCsvModel.builder().build();
    assertThat(lineUpdateCsvModel.validate()).hasSize(3);
  }

  @Test
  void shouldCheckUniqueFieldsInLineUpdateCsvModel() {
    LineUpdateCsvModel bern = LineUpdateCsvModel.builder().build();
    assertThat(bern.uniqueFields()).hasSize(1);
  }

}