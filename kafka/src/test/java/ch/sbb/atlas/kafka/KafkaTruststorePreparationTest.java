package ch.sbb.atlas.kafka;

import static org.assertj.core.api.Assertions.assertThat;

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