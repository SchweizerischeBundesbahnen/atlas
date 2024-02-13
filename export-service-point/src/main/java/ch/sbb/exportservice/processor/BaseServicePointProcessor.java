package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.servicepoint.Canton;
import ch.sbb.atlas.api.servicepoint.DistrictModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.atlas.api.servicepoint.LocalityMunicipalityModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class BaseServicePointProcessor {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN);
  public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(
      AtlasApiConstants.DATE_TIME_FORMAT_PATTERN);

  CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  protected static List<MeanOfTransport> getMeansOfTransportSorted(Set<MeanOfTransport> meanOfTransports) {
    return meanOfTransports.stream().sorted().toList();
  }

  protected static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
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

  public GeolocationBaseReadModel toModel(GeolocationBaseEntity geolocationBaseEntity) {
    if (geolocationBaseEntity == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(geolocationBaseEntity);
    return GeolocationBaseReadModel.builder()
        .spatialReference(geolocationBaseEntity.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .lv03(coordinates.get(SpatialReference.LV03))
        .height(geolocationBaseEntity.getHeight())
        .build();
  }

  public ServicePointGeolocationReadModel fromEntity(ServicePointGeolocation servicePointGeolocation) {
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

  public Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
    Map<SpatialReference, CoordinatePair> coordinates = new EnumMap<>(SpatialReference.class);

    Stream.of(SpatialReference.values()).forEach(spatialReference -> {
      if (spatialReference == entity.getSpatialReference()) {
        coordinates.put(spatialReference, entity.asCoordinatePair());
      } else {
        coordinates.put(spatialReference, coordinateTransformer.transform(entity.asCoordinatePair(), spatialReference));
      }
    });
    return coordinates;
  }

  protected String mapStandardAttributeType(StandardAttributeType attributeType){
    return attributeType != null ? attributeType.toString() : null;
  }

}
