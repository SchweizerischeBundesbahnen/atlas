package ch.sbb.exportservice.job.sepodi;

import ch.sbb.atlas.api.servicepoint.Canton;
import ch.sbb.atlas.api.servicepoint.DistrictModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.atlas.api.servicepoint.LocalityMunicipalityModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointGeolocation;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class BaseSepodiProcessor {

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  protected static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
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

  private Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
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
