package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinateTransformer;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeometryTransformer {

  private final CoordinateTransformer coordinateTransformer;

  private static final int WGS_84_WKID = SpatialReference.WGS84.getWellKnownId();

  public Coordinate transformToWeb(int sourceWKID, Coordinate wgsCoordinates) {
    CoordinatePair transformed = coordinateTransformer.transform(
        CoordinatePair
            .builder()
            .east(wgsCoordinates.getX())
            .north(wgsCoordinates.getY())
            .spatialReference(Stream.of(SpatialReference.values())
                                    .filter(sr -> sr.getWellKnownId() == sourceWKID)
                                    .findFirst()
                                    .get())
            .build(), SpatialReference.WGS84WEB);
    return new Coordinate(transformed.getEast(), transformed.getNorth());
  }

  public Envelope projectEnvelopeToWeb(Envelope tileEnvelope) {
    final Coordinate tileEnvelopeMin = transformToWeb(WGS_84_WKID,
        new Coordinate(tileEnvelope.getMinX(), tileEnvelope.getMinY()));
    final Coordinate tileEnvelopeMax = transformToWeb(WGS_84_WKID,
        new Coordinate(tileEnvelope.getMaxX(), tileEnvelope.getMaxY()));
    return new Envelope(
        tileEnvelopeMin.getX(),
        tileEnvelopeMax.getX(),
        tileEnvelopeMin.getY(),
        tileEnvelopeMax.getY());
  }
}
