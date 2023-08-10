package ch.sbb.atlas.servicepointdirectory.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.Test;

class ServicePointGeolocationBaseModelTest {

  @Test
  void shouldAllowNullInCountryForIsoCountryCode() {
    ServicePointGeolocationReadModel servicePointGeolocationModel = ServicePointGeolocationReadModel.builder().build();
    assertThat(servicePointGeolocationModel.getIsoCountryCode()).isNull();
  }

  @Test
  void shouldReturnGivenIsoCountryCode() {
    ServicePointGeolocationReadModel servicePointGeolocationModel =
        ServicePointGeolocationReadModel.builder().country(Country.GERMANY).build();
    assertThat(servicePointGeolocationModel.getIsoCountryCode()).isEqualTo("DE");
  }
}