package ch.sbb.atlas.model.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.junit.jupiter.api.Test;

@IntegrationTest
public class KafkaTruststorePreparationTest {

  @Test
  void shouldPrepareTruststore() {
    // When
    KafkaTruststorePreparation.setupTruststore();

    // Then
    assertThat(System.getProperty("KAFKA_TRUSTSTORE_LOCATION")).isNotEmpty();
  }
}