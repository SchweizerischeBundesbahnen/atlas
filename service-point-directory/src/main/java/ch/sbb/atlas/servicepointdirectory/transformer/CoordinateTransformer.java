package ch.sbb.atlas.servicepointdirectory.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
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

  public CoordinateTransformer() {
    final CRSFactory crsFactory = new CRSFactory();
    Stream.of(SpatialReference.values()).forEach(sr -> {
      referenceSystemMap.put(sr, crsFactory.createFromName("epsg:" + sr.getWellKnownId()));
    });
  }

  public CoordinatePair transform(CoordinatePair coordinatePair,
      SpatialReference targetSpatialReference) {
    if (coordinatePair == null) {
      throw new IllegalArgumentException("coordinatePair");
    }
    final SpatialReference sourceSpatialReference = coordinatePair.getSpatialReference();
    if (sourceSpatialReference == null) {
      throw new IllegalArgumentException("coordinatePair.sourceSpatialReference");
    }
    if (targetSpatialReference == null) {
      throw new IllegalArgumentException("targetSpatialReference");
    }

    final CoordinateTransform cTransform = ctFactory.createTransform(
        referenceSystemMap.get(sourceSpatialReference),
        referenceSystemMap.get(targetSpatialReference));
    final ProjCoordinate result = new ProjCoordinate();
    cTransform.transform(new ProjCoordinate(coordinatePair.getEast(), coordinatePair.getNorth()),
        result);

    return CoordinatePair.builder().north(result.y).east(result.x).build();

  }
}

