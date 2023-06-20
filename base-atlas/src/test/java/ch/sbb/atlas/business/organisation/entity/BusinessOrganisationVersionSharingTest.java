package ch.sbb.atlas.business.organisation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.model.Status;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class BusinessOrganisationVersionSharingTest {

  @Test
  void shouldSetAllPropertiesFromModel() {
    SharedBusinessOrganisationVersionModel model = SharedBusinessOrganisationVersionModel.builder()
        .id(1L)
        .abbreviationDe("de")
        .abbreviationFr("fr")
        .abbreviationIt("it")
        .abbreviationEn("en")
        .descriptionDe("de")
        .descriptionFr("fr")
        .descriptionIt("it")
        .descriptionEn("en")
        .organisationNumber(13)
        .status("VALIDATED")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusDays(1))
        .build();

    SharedBusinessOrganisationVersion sharedBusinessOrganisationVersion = new SharedBusinessOrganisationVersion();
    sharedBusinessOrganisationVersion.setPropertiesFromModel(model);

    assertThat(sharedBusinessOrganisationVersion).usingRecursiveComparison().ignoringFields("status").isEqualTo(model);
    assertThat(sharedBusinessOrganisationVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @Builder
  private static class SharedBusinessOrganisationVersion implements BusinessOrganisationVersionSharing {

    private Long id;
    private String sboid;
    private String descriptionDe;
    private String descriptionFr;
    private String descriptionIt;
    private String descriptionEn;
    private String abbreviationDe;
    private String abbreviationFr;
    private String abbreviationIt;
    private String abbreviationEn;
    private Integer organisationNumber;
    private Status status;
    private LocalDate validFrom;
    private LocalDate validTo;

  }
}