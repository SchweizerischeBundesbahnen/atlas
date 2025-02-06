package ch.sbb.atlas.imports.model.create;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TrafficPointCreateCsvModelTest {

  @Test
  void shouldBeValidTrafficPointCreateModel() {
    TrafficPointCreateCsvModel platform = TrafficPointCreateCsvModel.builder()
        .stopPointSloid("ch:1:sloid:7000")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(platform.validate()).isEmpty();
  }

  @Test
  void shouldReportMissingElementType() {
    TrafficPointCreateCsvModel platform = TrafficPointCreateCsvModel.builder()
        .stopPointSloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(platform.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingStopPointSloid() {
    TrafficPointCreateCsvModel platform = TrafficPointCreateCsvModel.builder()
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(platform.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidFrom() {
    TrafficPointCreateCsvModel platform = TrafficPointCreateCsvModel.builder()
        .stopPointSloid("ch:1:sloid:7000")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(platform.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidTo() {
    TrafficPointCreateCsvModel platform = TrafficPointCreateCsvModel.builder()
        .stopPointSloid("ch:1:sloid:7000")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2021, 4, 1))
        .build();
    assertThat(platform.validate()).hasSize(1);
  }

}