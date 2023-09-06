package ch.sbb.atlas.servicepointdirectory.mapper;

import static ch.sbb.atlas.servicepointdirectory.mapper.GeolocationMapper.getTransformedCoordinates;

import ch.sbb.atlas.api.servicepoint.Canton;
import ch.sbb.atlas.api.servicepoint.DistrictModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.LocalityMunicipalityModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationCreateModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
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
        .lv03(coordinates.get(SpatialReference.LV03))
        .height(servicePointGeolocation.getHeight())
        .build();
  }

  public static ServicePointGeolocationCreateModel toCreateModel(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation == null) {
      return null;
    }
    return ServicePointGeolocationCreateModel.builder()
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
        .north(servicePointGeolocation.getNorth())
        .east(servicePointGeolocation.getEast())
        .height(servicePointGeolocation.getHeight())
        .build();
  }

  public static ServicePointGeolocation toEntity(GeolocationBaseCreateModel servicePointGeolocationModel) {
    if (servicePointGeolocationModel == null) {
      return null;
    }
    GeolocationMapper.transformLv03andWgs84(servicePointGeolocationModel);
    GeolocationMapper.checkIfCoordinatesAreTransformable(servicePointGeolocationModel);
    return ServicePointGeolocation.builder()
        .spatialReference(servicePointGeolocationModel.getSpatialReference())
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