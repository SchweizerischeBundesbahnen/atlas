package ch.sbb.exportservice.job.businessorganisation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisation;
import ch.sbb.exportservice.job.bodi.businessorganisation.processor.BusinessOrganisationJsonProcessor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BusinessOrganisationJsonProcessorTest {

  @Test
  void shouldMapToJsonCorrectly() {
    BusinessOrganisation businessOrganisation = BusinessOrganisation.builder()
        .id(5L)
        .sboid("ch:1:sboid:100000")
        .status(Status.VALIDATED)
        .abbreviationDe("abbreviationDe")
        .abbreviationFr("abbreviationFr")
        .abbreviationIt("abbreviationIt")
        .abbreviationEn("abbreviationEn")
        .descriptionDe("descriptionDe")
        .descriptionFr("descriptionFr")
        .descriptionIt("descriptionIt")
        .descriptionEn("descriptionEn")
        .organisationNumber(54)
        .contactEnterpriseEmail("mail@mail.com")
        .businessTypes(Set.of(BusinessType.RAILROAD))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .number("tunumber")
        .abbreviation("abbreviation")
        .businessRegisterName("businessRegisterName")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .version(0)
        .build();

    BusinessOrganisationVersionModel expected = BusinessOrganisationVersionModel.builder()
        .id(5L)
        .sboid("ch:1:sboid:100000")
        .status(Status.VALIDATED)
        .said("100000")
        .abbreviationDe("abbreviationDe")
        .abbreviationFr("abbreviationFr")
        .abbreviationIt("abbreviationIt")
        .abbreviationEn("abbreviationEn")
        .descriptionDe("descriptionDe")
        .descriptionFr("descriptionFr")
        .descriptionIt("descriptionIt")
        .descriptionEn("descriptionEn")
        .contactEnterpriseEmail("mail@mail.com")
        .businessTypes(Set.of(BusinessType.RAILROAD))
        .organisationNumber(54)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .etagVersion(0)
        .build();

    BusinessOrganisationVersionModel result = new BusinessOrganisationJsonProcessor().process(businessOrganisation);
    assertThat(result).isEqualTo(expected);
  }

}