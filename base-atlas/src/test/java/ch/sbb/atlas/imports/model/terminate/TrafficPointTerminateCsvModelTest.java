package ch.sbb.atlas.imports.model.terminate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TrafficPointTerminateCsvModelTest {

  @Test
  void shouldBeValidTrafficPointTerminateCsvModel() {
    TrafficPointTerminateCsvModel trafficPointTerminateCsvModel = TrafficPointTerminateCsvModel.builder()
        .sloid("ch:1:sloid")
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(trafficPointTerminateCsvModel.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInServicePointTerminateCsvModelSloid() {
    TrafficPointTerminateCsvModel trafficPointTerminateCsvModel = TrafficPointTerminateCsvModel.builder()
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(trafficPointTerminateCsvModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportErrorInServicePointTerminateCsvModel() {
    TrafficPointTerminateCsvModel trafficPointTerminateCsvModel = TrafficPointTerminateCsvModel.builder()
        .build();
    assertThat(trafficPointTerminateCsvModel.validate()).hasSize(2);
  }

  @Test
  void shouldCheckUniqueFieldsInServicePointTerminateCsvModel() {
    TrafficPointTerminateCsvModel trafficPointTerminateCsvModel = TrafficPointTerminateCsvModel.builder()
        .build();
    assertThat(trafficPointTerminateCsvModel.uniqueFields()).hasSize(1);
  }

}