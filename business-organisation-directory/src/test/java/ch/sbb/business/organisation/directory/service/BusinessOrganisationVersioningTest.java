package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class BusinessOrganisationVersioningTest {

  protected static final String SBOID = "ch:1:sboid:100000";

  private BusinessOrganisationVersion version1;
  private BusinessOrganisationVersion version2;
  private BusinessOrganisationVersion version3;
  private BusinessOrganisationVersion version4;

  @Autowired
  private BusinessOrganisationService service;

  @Autowired
  private BusinessOrganisationVersionRepository repository;

  @BeforeEach
  void init() {
    version1 = BusinessOrganisationVersion.builder()
                                          .sboid(SBOID)
                                          .abbreviationDe("de1")
                                          .abbreviationFr("fr1")
                                          .abbreviationIt("it1")
                                          .abbreviationEn("en1")
                                          .descriptionDe("desc-de1")
                                          .descriptionFr("desc-fr1")
                                          .descriptionIt("desc-it1")
                                          .descriptionEn("desc-en1")
                                          .businessTypes(new HashSet<>(
                                              Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                  BusinessType.SHIP)))
                                          .contactEnterpriseEmail("mail1@mail.ch")
                                          .organisationNumber(1234)
                                          .status(Status.VALIDATED)
                                          .validFrom(LocalDate.of(2020, 1, 1))
                                          .validTo(LocalDate.of(2021, 12, 31))
                                          .build();
    version2 = BusinessOrganisationVersion.builder()
                                          .sboid(SBOID)
                                          .abbreviationDe("de2")
                                          .abbreviationFr("fr1")
                                          .abbreviationIt("it1")
                                          .abbreviationEn("en1")
                                          .descriptionDe("desc-de1")
                                          .descriptionFr("desc-fr1")
                                          .descriptionIt("desc-it1")
                                          .descriptionEn("desc-en1")
                                          .businessTypes(new HashSet<>(
                                              Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                  BusinessType.SHIP)))
                                          .contactEnterpriseEmail("mail1@mail.ch")
                                          .organisationNumber(1234)
                                          .status(Status.VALIDATED)
                                          .validFrom(LocalDate.of(2022, 1, 1))
                                          .validTo(LocalDate.of(2023, 12, 31))
                                          .build();
    version3 = BusinessOrganisationVersion.builder()
                                          .sboid(SBOID)
                                          .abbreviationDe("de3")
                                          .abbreviationFr("fr1")
                                          .abbreviationIt("it1")
                                          .abbreviationEn("en1")
                                          .descriptionDe("desc-de1")
                                          .descriptionFr("desc-fr1")
                                          .descriptionIt("desc-it1")
                                          .descriptionEn("desc-en1")
                                          .businessTypes(new HashSet<>(
                                              Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                  BusinessType.SHIP)))
                                          .contactEnterpriseEmail("mail1@mail.ch")
                                          .organisationNumber(1234)
                                          .status(Status.VALIDATED)
                                          .validFrom(LocalDate.of(2024, 1, 1))
                                          .validTo(LocalDate.of(2024, 12, 31))
                                          .build();
    version4 = BusinessOrganisationVersion.builder()
                                          .sboid(SBOID)
                                          .abbreviationDe("de4")
                                          .abbreviationFr("fr1")
                                          .abbreviationIt("it1")
                                          .abbreviationEn("en1")
                                          .descriptionDe("desc-de1")
                                          .descriptionFr("desc-fr1")
                                          .descriptionIt("desc-it1")
                                          .descriptionEn("desc-en1")
                                          .businessTypes(new HashSet<>(
                                              Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                  BusinessType.SHIP)))
                                          .contactEnterpriseEmail("mail1@mail.ch")
                                          .organisationNumber(1234)
                                          .status(Status.VALIDATED)
                                          .validFrom(LocalDate.of(2025, 1, 1))
                                          .validTo(LocalDate.of(2025, 12, 31))
                                          .build();
  }

  @AfterEach
  void cleanUp() {
    List<BusinessOrganisationVersion> allBySboidOrderByValidFrom = repository.findAllBySboidOrderByValidFrom(
        SBOID);
    repository.deleteAll(allBySboidOrderByValidFrom);
  }

  /**
   * Szenario 5: Update, das über mehrere Versionen hinausragt
   *
   * NEU:             |___________________________________|
   * IST:      |-----------|-----------|-----------|-------------------
   * Version:        1           2          3               4
   *
   * RESULTAT: |------|_____|__________|____________|_____|------------     NEUE VERSION EINGEFÜGT
   * Version:      1     5       2           3         6      4
   */
  @Test
   void scenario5() {
    //given
    version1 = repository.save(version1);
    version2 = repository.save(version2);
    version3 = repository.save(version3);
    version4 = repository.save(version4);
    BusinessOrganisationVersion editedVersion =
        BusinessOrganisationVersion.builder()
        .sboid(SBOID)
        .abbreviationDe("de3")
        .abbreviationFr("fr1")
        .abbreviationIt("it1")
        .abbreviationEn("en1")
        .descriptionDe("Description <changed>")
        .descriptionFr("desc-fr1")
        .descriptionIt("desc-it1")
        .descriptionEn("desc-en1")
        .businessTypes(new HashSet<>(
            Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                BusinessType.SHIP,BusinessType.STREET)))
        .contactEnterpriseEmail("mail1@mail.ch")
        .organisationNumber(1234)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 6, 1))
        .validTo(LocalDate.of(2025, 6, 1))
        .build();

    //when
    service.updateBusinessOrganisationVersion(version3, editedVersion);
    List<BusinessOrganisationVersion> result = repository.findAllBySboidOrderByValidFrom(
        version1.getSboid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(6);
    result.sort(Comparator.comparing(BusinessOrganisationVersion::getValidFrom));

    // first current index updated
    assertThat(result.get(0)).isNotNull();
    BusinessOrganisationVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getAbbreviationFr()).isEqualTo("fr1");
    assertThat(firstTemporalVersion.getAbbreviationIt()).isEqualTo("it1");
    assertThat(firstTemporalVersion.getAbbreviationEn()).isEqualTo("en1");
    assertThat(firstTemporalVersion.getDescriptionDe()).isEqualTo("desc-de1");
    assertThat(firstTemporalVersion.getDescriptionFr()).isEqualTo("desc-fr1");
    assertThat(firstTemporalVersion.getDescriptionIt()).isEqualTo("desc-it1");
    assertThat(firstTemporalVersion.getDescriptionEn()).isEqualTo("desc-en1");
    assertThat(firstTemporalVersion.getBusinessTypes()).containsExactlyInAnyOrder(
        BusinessType.RAILROAD, BusinessType.AIR,BusinessType.SHIP);
    assertThat(firstTemporalVersion.getContactEnterpriseEmail()).isEqualTo("mail1@mail.ch");
    assertThat(firstTemporalVersion.getOrganisationNumber()).isEqualTo(1234);

    // new
    BusinessOrganisationVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getDescriptionDe()).isEqualTo("Description <changed>");
    assertThat(secondTemporalVersion.getAbbreviationFr()).isEqualTo("fr1");
    assertThat(secondTemporalVersion.getAbbreviationIt()).isEqualTo("it1");
    assertThat(secondTemporalVersion.getAbbreviationEn()).isEqualTo("en1");
    assertThat(secondTemporalVersion.getDescriptionFr()).isEqualTo("desc-fr1");
    assertThat(secondTemporalVersion.getDescriptionIt()).isEqualTo("desc-it1");
    assertThat(secondTemporalVersion.getDescriptionEn()).isEqualTo("desc-en1");
    assertThat(secondTemporalVersion.getBusinessTypes()).containsExactlyInAnyOrder(
        BusinessType.RAILROAD, BusinessType.AIR,BusinessType.SHIP,BusinessType.STREET);
    assertThat(secondTemporalVersion.getContactEnterpriseEmail()).isEqualTo("mail1@mail.ch");
    assertThat(secondTemporalVersion.getOrganisationNumber()).isEqualTo(1234);

    //update
    BusinessOrganisationVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getDescriptionDe()).isEqualTo("Description <changed>");
    assertThat(thirdTemporalVersion.getAbbreviationFr()).isEqualTo("fr1");
    assertThat(thirdTemporalVersion.getAbbreviationIt()).isEqualTo("it1");
    assertThat(thirdTemporalVersion.getAbbreviationEn()).isEqualTo("en1");
    assertThat(thirdTemporalVersion.getDescriptionFr()).isEqualTo("desc-fr1");
    assertThat(thirdTemporalVersion.getDescriptionIt()).isEqualTo("desc-it1");
    assertThat(thirdTemporalVersion.getDescriptionEn()).isEqualTo("desc-en1");
    assertThat(thirdTemporalVersion.getBusinessTypes()).containsExactlyInAnyOrder(
        BusinessType.RAILROAD, BusinessType.AIR,BusinessType.SHIP,BusinessType.STREET);
    assertThat(thirdTemporalVersion.getContactEnterpriseEmail()).isEqualTo("mail1@mail.ch");
    assertThat(thirdTemporalVersion.getOrganisationNumber()).isEqualTo(1234);

    //new
    BusinessOrganisationVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getDescriptionDe()).isEqualTo("Description <changed>");
    assertThat(fourthTemporalVersion.getAbbreviationFr()).isEqualTo("fr1");
    assertThat(fourthTemporalVersion.getAbbreviationIt()).isEqualTo("it1");
    assertThat(fourthTemporalVersion.getAbbreviationEn()).isEqualTo("en1");
    assertThat(fourthTemporalVersion.getDescriptionFr()).isEqualTo("desc-fr1");
    assertThat(fourthTemporalVersion.getDescriptionIt()).isEqualTo("desc-it1");
    assertThat(fourthTemporalVersion.getDescriptionEn()).isEqualTo("desc-en1");
    assertThat(fourthTemporalVersion.getBusinessTypes()).containsExactlyInAnyOrder(
        BusinessType.RAILROAD, BusinessType.AIR,BusinessType.SHIP,BusinessType.STREET);
    assertThat(fourthTemporalVersion.getContactEnterpriseEmail()).isEqualTo("mail1@mail.ch");
    assertThat(fourthTemporalVersion.getOrganisationNumber()).isEqualTo(1234);

    //new
    BusinessOrganisationVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getDescriptionDe()).isEqualTo("Description <changed>");
    assertThat(fifthTemporalVersion.getAbbreviationFr()).isEqualTo("fr1");
    assertThat(fifthTemporalVersion.getAbbreviationIt()).isEqualTo("it1");
    assertThat(fifthTemporalVersion.getAbbreviationEn()).isEqualTo("en1");
    assertThat(fifthTemporalVersion.getDescriptionFr()).isEqualTo("desc-fr1");
    assertThat(fifthTemporalVersion.getDescriptionIt()).isEqualTo("desc-it1");
    assertThat(fifthTemporalVersion.getDescriptionEn()).isEqualTo("desc-en1");
    assertThat(fifthTemporalVersion.getBusinessTypes()).containsExactlyInAnyOrder(
        BusinessType.RAILROAD, BusinessType.AIR,BusinessType.SHIP,BusinessType.STREET);
    assertThat(fifthTemporalVersion.getContactEnterpriseEmail()).isEqualTo("mail1@mail.ch");
    assertThat(fifthTemporalVersion.getOrganisationNumber()).isEqualTo(1234);

    //last current index updated
    BusinessOrganisationVersion sixthTemporalVersion = result.get(5);
    assertThat(sixthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
    assertThat(sixthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(sixthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(sixthTemporalVersion.getAbbreviationDe()).isEqualTo("de4");
    assertThat(sixthTemporalVersion.getAbbreviationFr()).isEqualTo("fr1");
    assertThat(sixthTemporalVersion.getAbbreviationIt()).isEqualTo("it1");
    assertThat(sixthTemporalVersion.getAbbreviationEn()).isEqualTo("en1");
    assertThat(sixthTemporalVersion.getDescriptionDe()).isEqualTo("desc-de1");
    assertThat(sixthTemporalVersion.getDescriptionFr()).isEqualTo("desc-fr1");
    assertThat(sixthTemporalVersion.getDescriptionIt()).isEqualTo("desc-it1");
    assertThat(sixthTemporalVersion.getDescriptionEn()).isEqualTo("desc-en1");
    assertThat(sixthTemporalVersion.getBusinessTypes()).containsExactlyInAnyOrder(
        BusinessType.RAILROAD, BusinessType.AIR,BusinessType.SHIP);
    assertThat(sixthTemporalVersion.getContactEnterpriseEmail()).isEqualTo("mail1@mail.ch");
    assertThat(sixthTemporalVersion.getOrganisationNumber()).isEqualTo(1234);

  }


}