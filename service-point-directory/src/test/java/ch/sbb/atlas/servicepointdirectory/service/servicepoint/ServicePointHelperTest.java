package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.StopPointNotLocatedInSwitzerlandException;
import org.junit.jupiter.api.Test;

class ServicePointHelperTest {

  @Test
  void shouldBeLocatedInSwitzerland(){
    //given
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    //when
    boolean result = ServicePointHelper.isStoPointLocatedInSwitzerland(servicePointVersion);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotBeLocatedInSwitzerland(){
    //given
    ServicePointVersion servicePointVersion = ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation();
    //when
    boolean result = ServicePointHelper.isStoPointLocatedInSwitzerland(servicePointVersion);
    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldBeGeolocationNull(){
    //given
    ServicePointVersion servicePointVersion = ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation();
    //when
    boolean result = ServicePointHelper.isGeolocationOrCountryNull(servicePointVersion);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldBeGeolocationCountryNull(){
    //given
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    servicePointVersion.getServicePointGeolocation().setCountry(null);
    //when
    boolean result = ServicePointHelper.isGeolocationOrCountryNull(servicePointVersion);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldValidateIsStopPointLocatedInSwitzerland(){
    //given
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    //when & then
    assertDoesNotThrow(() -> ServicePointHelper.validateIsStopPointLocatedInSwitzerland(servicePointVersion));
  }

  @Test
  void shouldNotValidateIsStopPointLocatedInSwitzerland(){
    //given
    ServicePointVersion servicePointVersion = ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation();
    //when & then
    assertThrows(StopPointNotLocatedInSwitzerlandException.class,
        () -> ServicePointHelper.validateIsStopPointLocatedInSwitzerland(servicePointVersion));
  }

}