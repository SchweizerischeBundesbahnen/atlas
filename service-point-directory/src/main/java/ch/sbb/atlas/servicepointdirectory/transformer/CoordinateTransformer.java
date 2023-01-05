package ch.sbb.atlas.servicepointdirectory.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.NonNull;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.stereotype.Service;

@Service
public class CoordinateTransformer {

  private final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
  private final Map<SpatialReference, CoordinateReferenceSystem> referenceSystemMap =
      new HashMap<>();
  private final Map<String, CoordinateTransform> coordinateTransformers = new HashMap<>();

  public CoordinateTransformer() {
    final CRSFactory crsFactory = new CRSFactory();
    Stream.of(SpatialReference.values())
          .forEach(sr -> referenceSystemMap
              .put(sr, crsFactory.createFromName("epsg:" + sr.getWellKnownId())));
  }

  /**
   * Transform source coordinate into given target spatial reference.
   *
   * @param sourceCoordinate       Source coordinate.
   * @param targetSpatialReference Target spatial reference system.
   * @return New transformed coordinate.
   */
  public CoordinatePair transform(
      @NonNull CoordinatePair sourceCoordinate,
      @NonNull SpatialReference targetSpatialReference) {
    final SpatialReference sourceSpatialReference = sourceCoordinate.getSpatialReference();
    if (sourceSpatialReference == null) {
      throw new NullPointerException("sourceCoordinate.sourceSpatialReference");
    }
    final ProjCoordinate result = new ProjCoordinate();
    findTransformer(
        referenceSystemMap.get(sourceSpatialReference),
        referenceSystemMap.get(targetSpatialReference))
        .transform(
            new ProjCoordinate(sourceCoordinate.getEast(), sourceCoordinate.getNorth()),
            result);

    return CoordinatePair.builder().north(result.y).east(result.x).build();

  }

  private CoordinateTransform findTransformer(
      CoordinateReferenceSystem source,
      CoordinateReferenceSystem target) {
    final String mappingName = source.getName() + target.getName();
    return coordinateTransformers.computeIfAbsent(mappingName,
        t -> ctFactory.createTransform(source, target));
  }
}

