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

  public CoordinatePair transform(SpatialReference sourceSpatialReference,
      SpatialReference targetSpatialReference, CoordinatePair coordinatePair) {

    final CoordinateTransform cTransform = ctFactory.createTransform(
        referenceSystemMap.get(sourceSpatialReference),
        referenceSystemMap.get(targetSpatialReference));
    final ProjCoordinate result = new ProjCoordinate();
    cTransform.transform(new ProjCoordinate(coordinatePair.getEast(), coordinatePair.getNorth()),
        result);

    return CoordinatePair.builder().north(result.y).east(result.x).build();

  }

  public CoordinatePair transform(SpatialReference sourceSpatialReference,
      SpatialReference targetSpatialReference, double east, double north) {
    return transform(sourceSpatialReference, targetSpatialReference,
        CoordinatePair.builder().east(east).north(north).build());
  }
}

