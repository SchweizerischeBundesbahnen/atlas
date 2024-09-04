package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;

@UtilityClass
public class ServicePointGeoLocationUtils {

  public static boolean hasDiffServicePointGeolocation(ServicePointGeolocation current,
      ServicePointGeolocation updated) {
    DiffResult<ServicePointGeolocation> geolocationDiffResult = getServicePointGeolocationDiffResult(
        current, updated);

    return geolocationDiffResult.getDiffs().size() > 0;
  }

  public static String getDiffServicePointGeolocationAsMessage(ServicePointGeolocation current,
      ServicePointGeolocation updated) {
    return getServicePointGeolocationDiffResult(current, updated).toString();
  }

  private static DiffResult<ServicePointGeolocation> getServicePointGeolocationDiffResult(ServicePointGeolocation current,
      ServicePointGeolocation updated) {
    return new DiffBuilder<>(current, updated,
        ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("Height", current.getHeight(), updated.getHeight())
        .append("Country", current.getCountry(), updated.getCountry())
        .append("Canton", current.getSwissCanton(), updated.getSwissCanton())
        .append("SwissDistrictNumber", current.getSwissDistrictNumber(), updated.getSwissDistrictNumber())
        .append("SwissDistrictName", current.getSwissDistrictName(), updated.getSwissDistrictName())
        .append("SwissMunicipalityNumber", current.getSwissMunicipalityNumber(), updated.getSwissMunicipalityNumber())
        .append("SwissMunicipalityName", current.getSwissMunicipalityName(), updated.getSwissMunicipalityName())
        .append("SwissLocalityName", current.getSwissLocalityName(), updated.getSwissLocalityName())
        .build();
  }


}
