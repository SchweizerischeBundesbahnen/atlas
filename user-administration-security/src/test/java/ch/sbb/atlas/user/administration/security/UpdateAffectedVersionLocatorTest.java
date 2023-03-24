package ch.sbb.atlas.user.administration.security;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpdateAffectedVersionLocatorTest {

  @Test
  void shouldWorkOnOneExcactMatch() {
    List<BusinessOrganisationAssociated> affectedVersions = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("previousValue").build()));
    assertThat(affectedVersions).hasSize(1);
  }

  @Test
  void shouldWorkOnOneExcactMatchWithMultiple() {
    List<BusinessOrganisationAssociated> affectedVersions = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("same").build(),
            BusinessObject.createDummy()
                .anotherValue("nextYear")
                .validFrom(LocalDate.of(2021, 1, 1))
                .validTo(LocalDate.of(2021, 12, 31))
                .build(), BusinessObject.createDummy()
                .anotherValue("inTwoYears")
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .build()));
    assertThat(affectedVersions).hasSize(1);
  }

  @Test
  void shouldWorkOnOverlap() {
    List<BusinessOrganisationAssociated> affectedVersions = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
        BusinessObject.createDummy()
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2022, 7, 31))
            .build(), List.of(BusinessObject.createDummy()
            .anotherValue("2021")
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .build(), BusinessObject.createDummy()
            .anotherValue(
                "2022")
            .validFrom(
                LocalDate.of(2022,
                    1, 1))
            .validTo(
                LocalDate.of(2022,
                    12, 31))
            .build()));
    assertThat(affectedVersions).hasSize(2);
  }

  @Test
  void shouldWorkOnProlongingBusinessObject() {
    List<BusinessOrganisationAssociated> affectedVersions = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
        BusinessObject.createDummy()
            .validFrom(LocalDate.of(2023, 1, 1))
            .validTo(LocalDate.of(2023, 12, 31))
            .build(), List.of(BusinessObject.createDummy()
            .anotherValue("2021")
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .build(), BusinessObject.createDummy()
            .anotherValue(
                "2022")
            .validFrom(
                LocalDate.of(2022,
                    1, 1))
            .validTo(
                LocalDate.of(2022,
                    12, 31))
            .build()));
    assertThat(affectedVersions).isEmpty();
  }
}