package ch.sbb.atlas.servicepointdirectory.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.NonNull;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.stereotype.Service;

@Service
public class CoordinateTransformer {

  private final CoordinateTransformFactory coordinateTransformFactory = new CoordinateTransformFactory();
  private final Map<SpatialReference, CoordinateReferenceSystem> referenceSystemMap =
      new HashMap<>();
  private final Map<String, CoordinateTransform> coordinateTransformers = new ConcurrentHashMap<>();

  public CoordinateTransformer() {
    final CRSFactory crsFactory = new CRSFactory();
    Stream.of(SpatialReference.values())
        .forEach(spatialReference -> referenceSystemMap
            .put(spatialReference, crsFactory.createFromName("epsg:" + spatialReference.getWellKnownId())));
  }

  /**
   * Transform source coordinate into given target spatial reference.
   *
   * @param sourceCoordinate Source coordinate.
   * @param targetSpatialReference Target spatial reference system.
   * @return New transformed coordinate.
   */
  public CoordinatePair transform(@NonNull @Valid CoordinatePair sourceCoordinate,
      @NonNull SpatialReference targetSpatialReference) {
    ProjCoordinate source = new ProjCoordinate(sourceCoordinate.getEast(), sourceCoordinate.getNorth());
    ProjCoordinate result = new ProjCoordinate();

    findTransformer(
        referenceSystemMap.get(sourceCoordinate.getSpatialReference()),
        referenceSystemMap.get(targetSpatialReference)
    ).transform(source, result);

    return CoordinatePair
        .builder()
        .north(result.y)
        .east(result.x)
        .spatialReference(targetSpatialReference)
        .build();
  }

  private CoordinateTransform findTransformer(CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
    final String mappingName = source.getName() + target.getName();
    return coordinateTransformers.computeIfAbsent(mappingName,
        t -> coordinateTransformFactory.createTransform(source, target));
  }
}

