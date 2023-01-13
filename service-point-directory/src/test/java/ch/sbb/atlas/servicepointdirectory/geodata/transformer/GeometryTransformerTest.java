package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class GeometryTransformerTest {

  private final GeometryTransformer geometryTransformer;

  @Autowired
  public GeometryTransformerTest(GeometryTransformer geometryTransformer) {
    this.geometryTransformer = geometryTransformer;
  }

  @Test
  void projectSwissEnvelopeToWeb() {
    final Offset<Double> doubleOffset = Offset.offset(0.00000001);

    final Envelope swissBounds = geometryTransformer.projectEnvelopeToWeb(
        new Envelope(5.96, 10.49, 45.82, 47.81));

    assertThat(swissBounds.getMinX()).isCloseTo(663464.1651279104, doubleOffset);
    assertThat(swissBounds.getMaxX()).isCloseTo(1167741.4584214399, doubleOffset);
    assertThat(swissBounds.getMinY()).isCloseTo(5751550.865005549, doubleOffset);
    assertThat(swissBounds.getMaxY()).isCloseTo(6075303.611974082, doubleOffset);
  }
}
