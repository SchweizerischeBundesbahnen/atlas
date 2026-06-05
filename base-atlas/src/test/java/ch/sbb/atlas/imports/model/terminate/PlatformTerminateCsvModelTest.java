package ch.sbb.atlas.imports.model.terminate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PlatformTerminateCsvModelTest {

  @Test
  void shouldBeValidPlatformTerminateCsvModel() {
    PlatformTerminateCsvModel platformTerminateCsvModel = PlatformTerminateCsvModel.builder()
        .sloid("ch:1:sloid")
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(platformTerminateCsvModel.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInPlatformTerminateCsvModelSloid() {
    PlatformTerminateCsvModel platformTerminateCsvModel = PlatformTerminateCsvModel.builder()
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(platformTerminateCsvModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportErrorInPlatformTerminateCsvModel() {
    PlatformTerminateCsvModel platformTerminateCsvModel = PlatformTerminateCsvModel.builder()
        .build();
    assertThat(platformTerminateCsvModel.validate()).hasSize(2);
  }

  @Test
  void shouldCheckUniqueFieldsInPlatformTerminateCsvModel() {
    PlatformTerminateCsvModel platformTerminateCsvModel = PlatformTerminateCsvModel.builder()
        .build();
    assertThat(platformTerminateCsvModel.uniqueFields()).hasSize(1);
  }

}
