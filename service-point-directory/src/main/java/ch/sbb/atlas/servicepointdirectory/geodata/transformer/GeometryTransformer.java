package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinateTransformer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeometryTransformer {

  private final CoordinateTransformer coordinateTransformer;

  public Coordinate transform(
      SpatialReference sourceSpatialReference,
      Coordinate sourceCoordinate,
      SpatialReference targetSpatialReference) {
    final CoordinatePair transformed = coordinateTransformer.transform(
        CoordinatePair
            .builder()
            .east(sourceCoordinate.getX())
            .north(sourceCoordinate.getY())
            .spatialReference(sourceSpatialReference)
            .build(),
        targetSpatialReference);
    return new Coordinate(transformed.getEast(), transformed.getNorth());
  }

  public Envelope projectArea(
      SpatialReference sourceSpatialReference,
      Envelope sourceArea,
      SpatialReference targetSpatialReference) {
    final Coordinate bottomLeftCorner = transform(sourceSpatialReference,
        new Coordinate(sourceArea.getMinX(), sourceArea.getMinY()),
        targetSpatialReference);
    final Coordinate topRightCorner = transform(sourceSpatialReference,
        new Coordinate(sourceArea.getMaxX(), sourceArea.getMaxY()),
        targetSpatialReference
    );
    return new Envelope(
        bottomLeftCorner.getX(),
        topRightCorner.getX(),
        bottomLeftCorner.getY(),
        topRightCorner.getY());
  }

  public Map<SpatialReference, Envelope> getProjectedAreas(Envelope areaWgs84) {
    final Map<SpatialReference, Envelope> projectedAreas = new HashMap<>();
    Stream.of(SpatialReference.values())
          .filter(sr -> !sr.equals(SpatialReference.WGS84))
          .forEach(sr ->
              projectedAreas.put(sr, projectArea(SpatialReference.WGS84, areaWgs84, sr))
          );
    return projectedAreas;
  }
}
