<<<<<<< HEAD:base-service/src/test/java/ch/sbb/atlas/base/service/model/service/KafkaTruststorePreparationTest.java
package ch.sbb.atlas.base.service.model.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
=======
package ch.sbb.atlas.kafka;

import static org.assertj.core.api.Assertions.assertThat;

>>>>>>> ATLAS-827: kafka-lib as a base for all:kafka/src/test/java/ch/sbb/atlas/kafka/KafkaTruststorePreparationTest.java
import org.junit.jupiter.api.Test;

public class KafkaTruststorePreparationTest {

  @Test
  void shouldPrepareTruststore() {
    // When
    KafkaTruststorePreparation.setupTruststore();

    // Then
    assertThat(System.getProperty("KAFKA_TRUSTSTORE_LOCATION")).isNotEmpty();
  }
}