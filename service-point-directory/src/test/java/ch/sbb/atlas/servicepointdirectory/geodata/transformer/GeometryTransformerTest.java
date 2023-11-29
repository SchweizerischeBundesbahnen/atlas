package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.util.Map;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class GeometryTransformerTest {

  static final Envelope SWISS_AREA_WGS_84 = new Envelope(5.96, 10.49, 45.82, 47.81);
  private final GeometryTransformer geometryTransformer;

  @Autowired
   GeometryTransformerTest(GeometryTransformer geometryTransformer) {
    this.geometryTransformer = geometryTransformer;
  }

  @Test
  void projectSwissEnvelopeToWeb() {
    final Offset<Double> doubleOffset = Offset.offset(0.00000001);

    final Envelope swissAreaWgs84Web = geometryTransformer.projectArea(
        SpatialReference.WGS84,
        SWISS_AREA_WGS_84,
        SpatialReference.WGS84WEB);

    assertThat(swissAreaWgs84Web.getMinX()).isCloseTo(663464.1651279104, doubleOffset);
    assertThat(swissAreaWgs84Web.getMaxX()).isCloseTo(1167741.4584214399, doubleOffset);
    assertThat(swissAreaWgs84Web.getMinY()).isCloseTo(5751550.865005549, doubleOffset);
    assertThat(swissAreaWgs84Web.getMaxY()).isCloseTo(6075303.611974082, doubleOffset);
  }

  @Test
  void projectSwissEnvelopeToEnvelopeMap() {
    final Map<SpatialReference, Envelope> envelopeMap = geometryTransformer
        .getProjectedAreas(SWISS_AREA_WGS_84);

    assertThat(envelopeMap).hasSize(4);
    assertThat(envelopeMap.containsKey(SpatialReference.WGS84WEB)).isTrue();
    assertThat(envelopeMap.containsKey(SpatialReference.WGS84)).isTrue();
    assertThat(envelopeMap.containsKey(SpatialReference.LV95)).isTrue();
    assertThat(envelopeMap.containsKey(SpatialReference.LV03)).isTrue();
  }
}
