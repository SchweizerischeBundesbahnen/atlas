package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TrafficPointUpdateCsvModelTest {

  @Test
  void shouldBeValidTrafficPointUpdateCsvModel() {
    TrafficPointUpdateCsvModel bern = TrafficPointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:5")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("Bern")
        .build();
    assertThat(bern.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInTrafficPointUpdateCsvModel() {
    TrafficPointUpdateCsvModel bern = TrafficPointUpdateCsvModel.builder()
        .build();
    assertThat(bern.validate()).hasSize(3);
  }

  @Test
  void shouldCheckUniqueFieldsInTrafficPointUpdateCsvModel() {
    TrafficPointUpdateCsvModel bern = TrafficPointUpdateCsvModel.builder()
        .build();
    assertThat(bern.uniqueFields()).hasSize(1);
  }

}
