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
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation.ServicePointGeolocationBuilder;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointGeolocationMapper {

  private static final CoordinateTransformer COORDINATE_TRANSFORMER = new CoordinateTransformer();

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
    GeolocationBaseCreateModel transformedModel = transformLv03andWgs84(servicePointGeolocationModel);
    return geolocationBuilder.spatialReference(
            transformedModel.getSpatialReference())
        .north(transformedModel.getNorth())
        .east(transformedModel.getEast())
        .height(servicePointGeolocationModel.getHeight())
        .build();
  }

  ServicePointGeolocationCreateModel transformLv03andWgs84(ServicePointGeolocationCreateModel servicePointGeolocationModel) {
    if (servicePointGeolocationModel.getSpatialReference() == SpatialReference.LV03) {
      CoordinatePair transformedCoordinates = COORDINATE_TRANSFORMER.transform(CoordinatePair.builder()
          .spatialReference(SpatialReference.LV03)
          .east(servicePointGeolocationModel.getEast())
          .north(servicePointGeolocationModel.getNorth())
          .build(), SpatialReference.LV95);
      servicePointGeolocationModel.setSpatialReference(SpatialReference.LV95);
      servicePointGeolocationModel.setEast(transformedCoordinates.getEast());
      servicePointGeolocationModel.setNorth(transformedCoordinates.getNorth());
      return servicePointGeolocationModel;
    }
    if (servicePointGeolocationModel.getSpatialReference() == SpatialReference.WGS84WEB) {
      CoordinatePair transformedCoordinates = COORDINATE_TRANSFORMER.transform(CoordinatePair.builder()
          .spatialReference(SpatialReference.WGS84WEB)
          .east(servicePointGeolocationModel.getEast())
          .north(servicePointGeolocationModel.getNorth())
          .build(), SpatialReference.WGS84);
      servicePointGeolocationModel.setSpatialReference(SpatialReference.WGS84);
      servicePointGeolocationModel.setEast(transformedCoordinates.getEast());
      servicePointGeolocationModel.setNorth(transformedCoordinates.getNorth());
      return servicePointGeolocationModel;
    }
    return servicePointGeolocationModel;
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