package ch.sbb.atlas.servicepointdirectory.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import org.junit.jupiter.api.Test;

class ServicePointGeolocationModelTest {

  @Test
  void shouldAllowNullInCountryForIsoCountryCode() {
    ServicePointGeolocationModel servicePointGeolocationModel = ServicePointGeolocationModel.builder().build();
    assertThat(servicePointGeolocationModel.getIsoCountryCode()).isNull();
  }

  @Test
  void shouldReturnGivenIsoCountryCode() {
    ServicePointGeolocationModel servicePointGeolocationModel =
        ServicePointGeolocationModel.builder().country(Country.GERMANY).build();
    assertThat(servicePointGeolocationModel.getIsoCountryCode()).isEqualTo("DE");
  }
}