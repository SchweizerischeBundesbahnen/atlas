package ch.sbb.atlas.servicepointdirectory.geodata.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile.Layer;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

class VectorTileServiceTest {

  private static final int DEFAULT_VECTOR_TILE_EXTENT = 4096;
  private static final int VECTOR_TILE_MOVE_TO_COMMAND_ENCODED = 9;
  private static final String TEST_LAYER_NAME = "test-layer";
  private VectorTileService vectorTileService;

  private GeometryFactory geometryFactory;

  private Envelope testEnvelope;

  @BeforeEach
  void initialize() {
    vectorTileService = new VectorTileService();
    geometryFactory = new GeometryFactory();
    testEnvelope = new Envelope(0, 10, 0, 10);
  }

  @Test
  void encodeTileLayerNoData() {
    final Tile tileLayer = vectorTileService.encodeTileLayer(
        TEST_LAYER_NAME,
        List.of(),
        testEnvelope);
    assertThat(tileLayer).isNotNull();
  }

  @Test
  void encodeTileLayerWithPointInTheMiddle() {
    final long centerX = Math.round((testEnvelope.getMaxX() - testEnvelope.getMinX()) / 2);
    final long centerY = Math.round((testEnvelope.getMaxY() - testEnvelope.getMinY()) / 2);
    final Point point1 = geometryFactory.createPoint(new Coordinate(centerX, centerY));
    point1.setSRID(SpatialReference.WGS84WEB.getWellKnownId());
    point1.setUserData(Map.of("id", 1000L));

    final int tileLayerCenter = DEFAULT_VECTOR_TILE_EXTENT / 2;
    final int tileLayerEncodedCenter = getZigZagValue(tileLayerCenter);

    final Tile tileLayer = vectorTileService.encodeTileLayer(
        TEST_LAYER_NAME,
        List.of(point1),
        testEnvelope);

    assertThat(tileLayer).isNotNull();
    assertThat(tileLayer.getLayersCount()).isEqualTo(1);
    Layer layer = tileLayer.getLayers(0);
    assertThat(layer.getName()).isEqualTo(TEST_LAYER_NAME);

    assertThat(layer.getFeaturesCount()).isEqualTo(1);
    Tile.Feature feature1 = layer.getFeatures(0);

    List<Integer> commands = feature1.getGeometryList();
    assertThat(commands).hasSize(3);
    assertThat(commands.get(0)).isEqualTo(VECTOR_TILE_MOVE_TO_COMMAND_ENCODED);
    assertThat(commands.get(1)).isEqualTo(tileLayerEncodedCenter);
    assertThat(commands.get(2)).isEqualTo(tileLayerEncodedCenter);
  }

  // https://developers.google.com/protocol-buffers/docs/encoding#signed-ints
  static int getZigZagValue(int position) {
    return position + position + ((position < 0) ? 1 : 0);
  }

}
