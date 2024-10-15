package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PlatformUpdateCsvModelTest {

  @Test
  void shouldBeValidPlatformUpdateCsvModel() {
    PlatformUpdateCsvModel bern = PlatformUpdateCsvModel.builder()
        .sloid("ch:1:sloid:88253:0:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .additionalInformation("Die Buslinie 160 Fahrtrichtung Münsingen Bahnhof Konolfingen "
            + "Dorf bedienen diese Haltekante.")
        .build();
    assertThat(bern.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInPlatformUpdateCsvModel() {
    PlatformUpdateCsvModel bern = PlatformUpdateCsvModel.builder().build();
    assertThat(bern.validate()).hasSize(3);
  }

  @Test
  void shouldCheckUniqueFieldsInPlatformUpdateCsvModel() {
    PlatformUpdateCsvModel bern = PlatformUpdateCsvModel.builder().build();
    assertThat(bern.uniqueFields()).hasSize(1);
  }

}
