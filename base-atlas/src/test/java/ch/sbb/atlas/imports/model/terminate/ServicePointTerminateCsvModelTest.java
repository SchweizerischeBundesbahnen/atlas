package ch.sbb.atlas.imports.model.terminate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ServicePointTerminateCsvModelTest {

  @Test
  void shouldBeValidServicePointTerminateCsvModel() {
    ServicePointTerminateCsvModel servicePointTerminateCsvModel = ServicePointTerminateCsvModel.builder()
        .sloid("ch:1:sloid:88253")
        .number(8507000)
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    assertThat(servicePointTerminateCsvModel.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorInServicePointTerminateCsvModel() {
    ServicePointTerminateCsvModel servicePointTerminateCsvModel = ServicePointTerminateCsvModel.builder()
        .build();
    assertThat(servicePointTerminateCsvModel.validate()).hasSize(3);
  }

  @Test
  void shouldCheckUniqueFieldsInServicePointTerminateCsvModel() {
    ServicePointTerminateCsvModel servicePointTerminateCsvModel = ServicePointTerminateCsvModel.builder()
        .build();
    assertThat(servicePointTerminateCsvModel.uniqueFields()).hasSize(2);
  }

}
