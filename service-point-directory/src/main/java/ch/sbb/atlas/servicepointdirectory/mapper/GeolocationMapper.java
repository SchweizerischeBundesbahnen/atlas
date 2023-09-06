package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.atlas.api.servicepoint.TransformableGeolocation;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.CoordinatesNotTransformableException;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GeolocationMapper {

  private static final CoordinateTransformer COORDINATE_TRANSFORMER = new CoordinateTransformer();

  public static GeolocationBaseReadModel toModel(GeolocationBaseEntity geolocationBaseEntity) {
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
    GeolocationMapper.transformLv03andWgs84(geolocationBaseModel);
    GeolocationMapper.checkIfCoordinatesAreTransformable(geolocationBaseModel);
    return TrafficPointElementGeolocation.builder()
        .spatialReference(geolocationBaseModel.getSpatialReference())
        .east(geolocationBaseModel.getEast())
        .north(geolocationBaseModel.getNorth())
        .height(geolocationBaseModel.getHeight())
        .build();
  }

  static public Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
    Map<SpatialReference, CoordinatePair> coordinates = new EnumMap<>(SpatialReference.class);

    Stream.of(SpatialReference.values()).forEach(spatialReference -> {
      if (spatialReference == entity.getSpatialReference()) {
        coordinates.put(spatialReference, entity.asCoordinatePair());
      } else {
        coordinates.put(spatialReference, COORDINATE_TRANSFORMER.transform(entity.asCoordinatePair(), spatialReference));
      }
    });
    return coordinates;
  }

  public static <T extends TransformableGeolocation> void transformLv03andWgs84(T geolocation) {
    if (geolocation.getEast() != null && geolocation.getNorth() != null) {
      if (geolocation.getSpatialReference() == SpatialReference.LV03) {
        CoordinatePair transformedCoordinates = COORDINATE_TRANSFORMER.transform(CoordinatePair.builder()
            .spatialReference(geolocation.getSpatialReference())
            .east(geolocation.getEast())
            .north(geolocation.getNorth())
            .build(), SpatialReference.LV95);
        geolocation.setSpatialReference(SpatialReference.LV95);
        geolocation.setEast(transformedCoordinates.getEast());
        geolocation.setNorth(transformedCoordinates.getNorth());
      }
      if (geolocation.getSpatialReference() == SpatialReference.WGS84WEB) {
        CoordinatePair transformedCoordinates = COORDINATE_TRANSFORMER.transform(CoordinatePair.builder()
            .spatialReference(geolocation.getSpatialReference())
            .east(geolocation.getEast())
            .north(geolocation.getNorth())
            .build(), SpatialReference.WGS84);
        geolocation.setSpatialReference(SpatialReference.WGS84);
        geolocation.setEast(transformedCoordinates.getEast());
        geolocation.setNorth(transformedCoordinates.getNorth());
      }
    }
  }

  public static void checkIfCoordinatesAreTransformable(TransformableGeolocation geolocation) {
    try {
      Stream.of(SpatialReference.values()).forEach(spatialReference -> COORDINATE_TRANSFORMER.transform(CoordinatePair.builder()
          .east(geolocation.getEast())
          .north(geolocation.getNorth())
          .spatialReference(geolocation.getSpatialReference())
          .build(), spatialReference));
    } catch (IllegalStateException exception) {
      throw new CoordinatesNotTransformableException(exception);
    }
  }
}
