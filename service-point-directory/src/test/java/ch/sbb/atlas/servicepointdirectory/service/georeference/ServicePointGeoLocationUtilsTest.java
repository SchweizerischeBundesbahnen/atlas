package ch.sbb.atlas.servicepointdirectory.service.georeference;

import static ch.sbb.atlas.servicepointdirectory.service.georeference.ServicePointGeoLocationUtils.getDiffServicePointGeolocationAsMessage;
import static ch.sbb.atlas.servicepointdirectory.service.georeference.ServicePointGeoLocationUtils.hasDiffServicePointGeolocation;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import org.junit.jupiter.api.Test;

class ServicePointGeoLocationUtilsTest {

  @Test
  void shouldHaveDiff() {
    //given
    ServicePointGeolocation servicePointGeolocationBern = ServicePointTestData.getServicePointGeolocationBernMittelland();
    ServicePointGeolocation servicePointGeolocationAargau = ServicePointTestData.getAargauServicePointGeolocation();
    //when
    boolean result = hasDiffServicePointGeolocation(servicePointGeolocationBern, servicePointGeolocationAargau);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotHaveDiff() {
    //given
    ServicePointGeolocation servicePointGeolocationBern = ServicePointTestData.getServicePointGeolocationBernMittelland();
    //when
    boolean result = hasDiffServicePointGeolocation(servicePointGeolocationBern, servicePointGeolocationBern);
    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldGetDiff() {
    //given
    ServicePointGeolocation servicePointGeolocationBern = ServicePointTestData.getServicePointGeolocationBernMittelland();
    ServicePointGeolocation servicePointGeolocationAargau = ServicePointTestData.getAargauServicePointGeolocation();
    //when
    String result = getDiffServicePointGeolocationAsMessage(servicePointGeolocationBern, servicePointGeolocationAargau);
    //then
    assertThat(result).isNotNull().isEqualTo(
        "[Height=555.0,Canton=BERN,SwissDistrictNumber=246,SwissDistrictName=Bern-Mittelland,SwissMunicipalityNumber=351,"
            + "SwissMunicipalityName=Bern,SwissLocalityName=Bern] differs from [Height=<null>,Canton=AARGAU,"
            + "SwissDistrictNumber=1909,SwissDistrictName=Rheinfelden,SwissMunicipalityNumber=<null>,"
            + "SwissMunicipalityName=Hellikon,SwissLocalityName=Hellikon]");
  }

  @Test
  void shouldNotGetDiff() {
    //given
    ServicePointGeolocation servicePointGeolocationBern = ServicePointTestData.getServicePointGeolocationBernMittelland();
    //when
    String result = getDiffServicePointGeolocationAsMessage(servicePointGeolocationBern, servicePointGeolocationBern);
    //then
    assertThat(result).isNotNull().isEmpty();
  }

}