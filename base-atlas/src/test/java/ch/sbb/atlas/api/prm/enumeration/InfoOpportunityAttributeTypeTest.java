package ch.sbb.atlas.api.prm.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InfoOpportunityAttributeTypeTest {

  @Test
  void shouldGetTypeFromValue() {
    assertThat(InfoOpportunityAttributeType.of(0)).isEqualTo(InfoOpportunityAttributeType.TO_BE_COMPLETED);
    assertThat(InfoOpportunityAttributeType.of(15)).isEqualTo(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION);
  }

}