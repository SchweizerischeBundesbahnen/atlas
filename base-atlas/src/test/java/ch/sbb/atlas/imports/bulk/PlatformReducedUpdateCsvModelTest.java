package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PlatformReducedUpdateCsvModelTest {

  @Test
  void shouldBeValidPlatformUpdateCsvModel() {
    PlatformReducedUpdateCsvModel bern = PlatformReducedUpdateCsvModel.builder()
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
    PlatformReducedUpdateCsvModel bern = PlatformReducedUpdateCsvModel.builder().build();
    assertThat(bern.validate()).hasSize(3);
  }

  @Test
  void shouldCheckUniqueFieldsInPlatformUpdateCsvModel() {
    PlatformReducedUpdateCsvModel bern = PlatformReducedUpdateCsvModel.builder().build();
    assertThat(bern.uniqueFields()).hasSize(1);
  }

}
