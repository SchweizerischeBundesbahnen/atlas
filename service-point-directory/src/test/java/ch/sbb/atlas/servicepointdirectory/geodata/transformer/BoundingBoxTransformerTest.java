package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class BoundingBoxTransformerTest {

  private final BoundingBoxTransformer boundingBoxTransformer;

  @Autowired
   BoundingBoxTransformerTest(BoundingBoxTransformer boundingBoxTransformer) {
    this.boundingBoxTransformer = boundingBoxTransformer;
  }

  @Test
  void calculateBoundingBox() {
    final Envelope envelope = boundingBoxTransformer.calculateBoundingBox(0, 0, 0);
    assertThat(envelope.getMinX()).isEqualTo(-180);
    assertThat(envelope.getMaxX()).isEqualTo(180);
    final Offset<Double> doubleOffset = Offset.offset(0.1);
    assertThat(envelope.getMinY()).isCloseTo(-85, doubleOffset);
    assertThat(envelope.getMaxY()).isCloseTo(85, doubleOffset);
  }

  @Test
  void calculateBoundingBoxKirchberg() {
    final Envelope envelope = boundingBoxTransformer.calculateBoundingBox(18, 136592, 92110);
    final Offset<Double> doubleOffset = Offset.offset(0.000000001);
    assertThat(envelope.getMinX()).isCloseTo(7.5805664062, doubleOffset);
    assertThat(envelope.getMaxX()).isCloseTo(7.5819396972, doubleOffset);
    assertThat(envelope.getMinY()).isCloseTo(47.086020441, doubleOffset);
    assertThat(envelope.getMaxY()).isCloseTo(47.086955506, doubleOffset);
  }
}
