package ch.sbb.atlas.servicepointdirectory.geodata.service;

import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.VectorTileEncoder;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
public class VectorTileService {

  public Tile encodeTileLayer(String layerName, List<Point> geometries, Envelope tileEnvelopeWeb) {
    VectorTileEncoder vectorTileEncoder = new VectorTileEncoder();
    vectorTileEncoder.setTileEnvelope(tileEnvelopeWeb);
    geometries.forEach(
        geometry -> vectorTileEncoder.addFeature(
            layerName,
            (Map<String, Object>) geometry.getUserData(),
            geometry));
    return vectorTileEncoder.encode();
  }
}
