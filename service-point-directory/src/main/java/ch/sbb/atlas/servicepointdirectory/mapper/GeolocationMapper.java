package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import lombok.experimental.UtilityClass;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

@UtilityClass
public class GeolocationMapper {

  public static GeolocationBaseReadModel toModel(GeolocationBaseEntity geolocationBaseEntity) {
    if (geolocationBaseEntity == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(geolocationBaseEntity);
    return GeolocationBaseReadModel.builder()
        .spatialReference(geolocationBaseEntity.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .height(geolocationBaseEntity.getHeight())
        .build();
  }

  public static GeolocationBaseCreateModel toCreateModel(GeolocationBaseEntity geolocationBaseEntity) {
    if (geolocationBaseEntity == null) {
      return null;
    }
    return GeolocationBaseCreateModel.builder()
        .spatialReference(geolocationBaseEntity.getSpatialReference())
        .east(geolocationBaseEntity.getEast())
        .north(geolocationBaseEntity.getNorth())
        .height(geolocationBaseEntity.getHeight())
        .build();
  }

  public static TrafficPointElementGeolocation toTrafficPointElementEntity(GeolocationBaseCreateModel geolocationBaseModel) {
    if (geolocationBaseModel == null) {
      return null;
    }
    return TrafficPointElementGeolocation.builder()
        .spatialReference(geolocationBaseModel.getSpatialReference())
        .east(geolocationBaseModel.getEast())
        .north(geolocationBaseModel.getNorth())
        .height(geolocationBaseModel.getHeight())
        .build();
  }

  static public Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
    Map<SpatialReference, CoordinatePair> coordinates = new EnumMap<>(SpatialReference.class);

    CoordinateTransformer coordinateTransformer = new CoordinateTransformer();
    Stream.of(SpatialReference.values()).forEach(spatialReference -> {
      if (spatialReference == entity.getSpatialReference()) {
        coordinates.put(spatialReference, entity.asCoordinatePair());
      } else {
        coordinates.put(spatialReference, coordinateTransformer.transform(entity.asCoordinatePair(), spatialReference));
      }
    });
    return coordinates;
  }

}
