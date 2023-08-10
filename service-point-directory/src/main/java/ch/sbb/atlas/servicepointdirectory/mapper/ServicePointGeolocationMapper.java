package ch.sbb.atlas.servicepointdirectory.mapper;

import static ch.sbb.atlas.servicepointdirectory.mapper.GeolocationMapper.getTransformedCoordinates;

import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationCreateModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.api.servicepoint.SwissLocation.Canton;
import ch.sbb.atlas.api.servicepoint.SwissLocation.DistrictModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation.LocalityMunicipalityModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation.ServicePointGeolocationBuilder;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointGeolocationMapper {

  public static ServicePointGeolocationReadModel toModel(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(servicePointGeolocation);
    return ServicePointGeolocationReadModel.builder()
        .country(servicePointGeolocation.getCountry())
        .swissLocation(SwissLocation.builder()
            .canton(servicePointGeolocation.getSwissCanton())
            .cantonInformation(getCanton(servicePointGeolocation))
            .district(DistrictModel.builder()
                .fsoNumber(servicePointGeolocation.getSwissDistrictNumber())
                .districtName(servicePointGeolocation.getSwissDistrictName())
                .build())
            .localityMunicipality(LocalityMunicipalityModel.builder()
                .fsoNumber(servicePointGeolocation.getSwissMunicipalityNumber())
                .municipalityName(servicePointGeolocation.getSwissMunicipalityName())
                .localityName(servicePointGeolocation.getSwissLocalityName())
                .build())
            .build())
        .spatialReference(servicePointGeolocation.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .height(servicePointGeolocation.getHeight())
        .build();
  }

  public static ServicePointGeolocation toEntity(ServicePointGeolocationCreateModel servicePointGeolocationModel) {
    ServicePointGeolocationBuilder<?, ?> geolocationBuilder = ServicePointGeolocation.builder()
        .country(servicePointGeolocationModel.getCountry());
    if (servicePointGeolocationModel.getSwissLocation() != null) {
      geolocationBuilder
          .swissCanton(servicePointGeolocationModel.getSwissLocation().getCanton())
          .swissDistrictName(servicePointGeolocationModel.getSwissLocation().getDistrict().getDistrictName())
          .swissDistrictNumber(servicePointGeolocationModel.getSwissLocation().getDistrict().getFsoNumber())
          .swissMunicipalityNumber(servicePointGeolocationModel.getSwissLocation().getLocalityMunicipality().getFsoNumber())
          .swissMunicipalityName(servicePointGeolocationModel.getSwissLocation().getLocalityMunicipality().getMunicipalityName())
          .swissLocalityName(servicePointGeolocationModel.getSwissLocation().getLocalityMunicipality().getLocalityName());
    }
    return geolocationBuilder.spatialReference(
        servicePointGeolocationModel.getSpatialReference())
        .north(servicePointGeolocationModel.getNorth())
        .east(servicePointGeolocationModel.getEast())
        .height(servicePointGeolocationModel.getHeight())
        .build();
  }

  private static Canton getCanton(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation.getSwissCanton() == null) {
      return null;
    }
    return Canton.builder()
        .abbreviation(servicePointGeolocation.getSwissCanton().getAbbreviation())
        .fsoNumber(servicePointGeolocation.getSwissCanton().getNumber())
        .name(servicePointGeolocation.getSwissCanton().getName())
        .build();
  }
}