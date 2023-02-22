package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinateTransformer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "Geolocation")
public class GeolocationModel {

  @Schema(description = "Coordinate system spatial reference", example = "LV95")
  @NotNull
  private SpatialReference spatialReference;

  @NotNull
  private CoordinatePair lv95;

  @NotNull
  private CoordinatePair wgs84;

  @NotNull
  private CoordinatePair wgs84web;

  @Schema(description = "Height of the coordinate point", example = "540.2")
  private Double height;

  public static GeolocationModel fromEntity(GeolocationBaseEntity geolocation) {
    if (geolocation == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(geolocation);
    return GeolocationModel.builder()
        .spatialReference(geolocation.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .wgs84web(coordinates.get(SpatialReference.WGS84WEB))
        .height(geolocation.getHeight())
        .build();
  }

  static Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
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
