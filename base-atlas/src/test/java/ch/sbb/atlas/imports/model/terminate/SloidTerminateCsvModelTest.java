package ch.sbb.atlas.imports.model.terminate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SloidTerminateCsvModelTest {

  @Test
  void shouldBeValidSloidTerminateCsvModel() {
    SloidTerminateCsvModel sloidTerminateCsvModel = SloidTerminateCsvModel.builder()
        .sloid("ch:1:sloid")
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(sloidTerminateCsvModel.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInSloidTerminateCsvModelSloid() {
    SloidTerminateCsvModel sloidTerminateCsvModel = SloidTerminateCsvModel.builder()
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(sloidTerminateCsvModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportErrorInSloidTerminateCsvModel() {
    SloidTerminateCsvModel sloidTerminateCsvModel = SloidTerminateCsvModel.builder()
        .build();
    assertThat(sloidTerminateCsvModel.validate()).hasSize(2);
  }

  @Test
  void shouldCheckUniqueFieldsInSloidTerminateCsvModel() {
    SloidTerminateCsvModel sloidTerminateCsvModel = SloidTerminateCsvModel.builder()
        .build();
    assertThat(sloidTerminateCsvModel.uniqueFields()).hasSize(1);
  }

}
