package ch.sbb.atlas.imports.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PlatformCompleteUpdateCsvModelTest {

  @Test
  void shouldBeValidPlatformCompleteUpdateCsvModel() {
    PlatformCompleteUpdateCsvModel platformCompleteUpdateCsvModel = PlatformCompleteUpdateCsvModel.builder()
        .sloid("ch:1:sloid:88253:0:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(platformCompleteUpdateCsvModel.validate()).isEmpty();
  }

}
