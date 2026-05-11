package ch.sbb.atlas.imports.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SublineUpdateCsvModelTest {

  @Test
  void shouldBeValidSublineUpdateCsvModel() {
    SublineUpdateCsvModel sublineUpdateCsvModel = SublineUpdateCsvModel.builder()
        .slnid("ch:1:sloid:88253:0:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(sublineUpdateCsvModel.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInSublineUpdateCsvModel() {
    SublineUpdateCsvModel sublineUpdateCsvModel = SublineUpdateCsvModel.builder().build();
    assertThat(sublineUpdateCsvModel.validate()).hasSize(3);
  }

  @Test
  void shouldCheckUniqueFieldsInSublineUpdateCsvModel() {
    SublineUpdateCsvModel sublineUpdateCsvModel = SublineUpdateCsvModel.builder().build();
    assertThat(sublineUpdateCsvModel.uniqueFields()).hasSize(1);
  }
}