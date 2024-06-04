package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.StopPointNotLocatedInSwitzerlandException;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointHelper {

  public boolean isStoPointLocatedInSwitzerland(ServicePointVersion servicePointVersion) {
    boolean isStopPoint = servicePointVersion.isStopPoint();
    boolean isSwissCountryCode = Objects.equals(servicePointVersion.getCountry().getUicCode(),
        Country.SWITZERLAND.getUicCode());
    boolean isSwissLocation = isSPLocatedInSwitzerland(servicePointVersion);
    return isSwissCountryCode && isStopPoint && isSwissLocation;
  }

  public static boolean isGeolocationOrCountryNull(ServicePointVersion newServicePointVersion) {
    return newServicePointVersion.getServicePointGeolocation() == null
        || newServicePointVersion.getServicePointGeolocation().getCountry() == null;
  }

  public void validateIsStopPointLocatedInSwitzerland(ServicePointVersion servicePointVersion) {
    boolean stoPointLocatedInSwitzerland = isStoPointLocatedInSwitzerland(servicePointVersion);
    if (!stoPointLocatedInSwitzerland) {
      throw new StopPointNotLocatedInSwitzerlandException(servicePointVersion.getSloid());
    }
  }

  private boolean isSPLocatedInSwitzerland(ServicePointVersion newServicePointVersion) {
    if (isGeolocationOrCountryNull(newServicePointVersion)) {
      return false;
    }
    ServicePointGeolocation servicePointGeolocation = newServicePointVersion.getServicePointGeolocation();
    return servicePointGeolocation.getCountry().equals(Country.SWITZERLAND);
  }

}
