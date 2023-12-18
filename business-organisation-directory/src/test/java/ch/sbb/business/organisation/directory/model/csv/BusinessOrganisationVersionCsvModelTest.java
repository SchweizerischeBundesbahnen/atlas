package ch.sbb.business.organisation.directory.model.csv;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.export.AtlasCsvMapper;
import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessOrganisationVersionCsvModelTest {

  private final ObjectWriter objectWriter = new AtlasCsvMapper(BusinessOrganisationVersionCsvModel.class).getObjectWriter();

  @Test
  void shouldExportToCorrectTimestampFormat() throws JsonProcessingException {
    BusinessOrganisationVersionCsvModel csvModel = BusinessOrganisationVersionCsvModel.toCsvModel(
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .sboid("ch:1:sboid:1000000")
            .abbreviationDe("de")
            .abbreviationFr("fr")
            .abbreviationIt("it")
            .abbreviationEn("en")
            .descriptionDe("desc-de")
            .descriptionFr("desc-fr")
            .descriptionIt("desc-it")
            .descriptionEn("desc-en")
            .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
            .contactEnterpriseEmail("mail@mail.ch")
            .organisationNumber(123)
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .creationDate(LocalDateTime.of(2020, 1, 31, 11, 12, 15, 147))
            .build());

    String csvString = objectWriter.writeValueAsString(csvModel);
    String[] csvSplit = csvString.split(";");
    String creationTimeAsString = csvSplit[csvSplit.length - 2].replaceAll("\"", "");

    assertThat(creationTimeAsString).isEqualTo("2020-01-31 11:12:15");
  }

  @Test
  void shouldExportToCorrectCsvFormat() throws JsonProcessingException {
    BusinessOrganisationVersionCsvModel csvModel = BusinessOrganisationVersionCsvModel.toCsvModel(
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .sboid("ch:1:sboid:1000000")
            .abbreviationDe("de")
            .abbreviationFr("fr")
            .abbreviationIt("it")
            .abbreviationEn("en")
            .descriptionDe("desc-de")
            .descriptionFr("desc-fr")
            .descriptionIt("desc-it")
            .descriptionEn("desc-en")
            .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
            .contactEnterpriseEmail("mail@mail.ch")
            .organisationNumber(123)
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .creationDate(LocalDateTime.of(2020, 1, 31, 11, 12, 15, 147))
            .editionDate(LocalDateTime.of(2020, 1, 31, 11, 12, 15, 147))
            .number("#001")
            .abbreviation("SBB")
            .businessRegisterName("Schweizer Bundesbahnen")
            .build());

    String csvString = objectWriter.writeValueAsString(csvModel);

    String expectedCsv = """
        sboid;said;validFrom;validTo;organisationNumber;status;descriptionDe;descriptionFr;descriptionIt;descriptionEn;abbreviationDe;abbreviationFr;abbreviationIt;abbreviationEn;businessTypesId;businessTypesDe;businessTypesIt;businessTypesFr;transportCompanyNumber;"transportCompanyAbbreviation";"transportCompanyBusinessRegisterName";creationTime;editionTime
        "ch:1:sboid:1000000";"1000000";"2000-01-01";"2000-12-31";123;VALIDATED;"desc-de";"desc-fr";"desc-it";"desc-en";de;fr;it;en;"10,20,45";"Eisenbahn,Schiff,Luft";"Ferrovia,Nave,Aria";"Chemin de fer,Bateau,Air";"#001";SBB;"Schweizer Bundesbahnen";"2020-01-31 11:12:15";"2020-01-31 11:12:15"
        """;
    assertThat(csvString).isEqualTo(expectedCsv);
  }
}