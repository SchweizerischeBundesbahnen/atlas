package ch.sbb.atlas.api.prm.model.platform;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformVersionModelTest {

  @Test
  void shouldReturnInfoOpportunitiesAsList() {
    PlatformVersionModel platformVersionModel = new PlatformVersionModel();
    assertThat(platformVersionModel.getInfoOpportunities()).isNotNull().isEmpty();
  }

  @Test
  void shouldReturnInfoOpportunitiesAsListWithEntries() {
    PlatformVersionModel platformVersionModel = PlatformVersionModel.builder()
        .infoOpportunities(List.of(InfoOpportunityAttributeType.ACOUSTIC_INFORMATION,
            InfoOpportunityAttributeType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE))
        .build();
    assertThat(platformVersionModel.getInfoOpportunities()).isNotNull().hasSize(2);
  }
}