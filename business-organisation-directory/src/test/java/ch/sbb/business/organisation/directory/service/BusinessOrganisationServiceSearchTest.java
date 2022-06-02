package ch.sbb.business.organisation.directory.service;

import static java.util.List.*;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.controller.BusinessOrganisationSearchRestrictions;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class BusinessOrganisationServiceSearchTest {

  private final BusinessOrganisationVersionRepository repository;
  private final BusinessOrganisationService service;

  private BusinessOrganisationVersion version1;
  private BusinessOrganisationVersion version2;
  private BusinessOrganisationVersion version3;

  @Autowired
  public BusinessOrganisationServiceSearchTest(BusinessOrganisationVersionRepository repository,
      BusinessOrganisationService service) {
    this.repository = repository;
    this.service = service;
  }

  @BeforeEach
  public void init() {
    version1 = BusinessOrganisationVersion.builder()
                                          .sboid("ch:1:sboid:100000")
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
                                          .status(Status.ACTIVE)
                                          .validFrom(LocalDate.of(2020, 1, 1))
                                          .validTo(LocalDate.of(2021, 12, 31))
                                          .build();
    version2 = BusinessOrganisationVersion.builder()
                                          .sboid("ch:1:sboid:100001")
                                          .abbreviationDe("de2")
                                          .abbreviationFr("fr2")
                                          .abbreviationIt("it2")
                                          .abbreviationEn("en2")
                                          .descriptionDe("desc-de2")
                                          .descriptionFr("desc-fr2")
                                          .descriptionIt("desc-it2")
                                          .descriptionEn("desc-en2")
                                          .businessTypes(new HashSet<>(
                                              Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                  BusinessType.SHIP)))
                                          .contactEnterpriseEmail("mail1@mail.ch")
                                          .organisationNumber(12345)
                                          .status(Status.ACTIVE)
                                          .validFrom(LocalDate.of(2022, 1, 1))
                                          .validTo(LocalDate.of(2023, 12, 31))
                                          .build();
    version3 = BusinessOrganisationVersion.builder()
                                          .sboid("ch:1:sboid:100003")
                                          .abbreviationDe("de3")
                                          .abbreviationFr("fr3")
                                          .abbreviationIt("it3")
                                          .abbreviationEn("en3")
                                          .descriptionDe("desc-de3")
                                          .descriptionFr("desc-fr3")
                                          .descriptionIt("desc-it3")
                                          .descriptionEn("desc-en3")
                                          .businessTypes(new HashSet<>(
                                              Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                  BusinessType.SHIP)))
                                          .contactEnterpriseEmail("mail1@mail.ch")
                                          .organisationNumber(12346)
                                          .status(Status.ACTIVE)
                                          .validFrom(LocalDate.of(2024, 1, 1))
                                          .validTo(LocalDate.of(2025, 12, 31))
                                          .build();
  }

  @AfterEach
  public void cleanUp() {
    repository.deleteAll();
  }

  @Test
  void shouldFindVersionWithValidOn() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .validOn(Optional.of(LocalDate.of(2020, 1, 1)))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithValidOn() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .validOn(Optional.of(LocalDate.of(2019, 1, 1)))
                                              .build());

    //then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindAllVersionOnNoRestrictions()  {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindOrderedVersionWithNoGivenValidOn()  {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(PageRequest.of(0, 20,
                                                  Sort.by("abbreviationDe")
                                                      .ascending()))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getAbbreviationDe()).isEqualTo("de1");
    assertThat(result.getContent().get(1).getAbbreviationDe()).isEqualTo("de2");
  }

  @Test
  void shouldFindDescOrderedVersionWithNoGivenValidOn()  {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(PageRequest.of(0, 20,
                                                  Sort.by("abbreviationDe")
                                                      .descending()))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getAbbreviationDe()).isEqualTo("de2");
    assertThat(result.getContent().get(1).getAbbreviationDe()).isEqualTo("de1");
  }

  @Test
  void shouldFindVersionWithText()  {
    //given
    repository.saveAndFlush(version1);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("de1"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithUnderscore()  {
    //given
    version1.setDescriptionDe("de1_");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("de1_"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleUnderscore()  {
    //given
    version1.setDescriptionDe("de1__");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("de1__"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithPercent()  {
    //given
    version1.setDescriptionDe("de1%");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("de1%"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultiplePercent()  {
    //given
    version1.setDescriptionDe("de1%%");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("de1%%"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleTexts()  {
    //given
    version1.setDescriptionDe("Forza Napoli sempre");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("1","Napoli", "Forza"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithMultipleTexts()  {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("1","Napoli", "Forza"))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(0);
  }

  @Test
  void shouldNotFindVersionWithStatus()  {
    //given
    version1.setDescriptionDe("Forza Napoli sempre");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of("1","Napoli", "Forza"))
                                              .statusRestrictions(List.of(Status.ACTIVE))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithMultipleStatus()  {
    //given
    repository.saveAndFlush(version1);
    version2.setStatus(Status.REVIEWED);
    repository.saveAndFlush(version2);
    version3.setStatus(Status.INACTIVE);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .statusRestrictions(List.of(Status.INACTIVE, Status.REVIEWED))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldFindVersionByOrganisationNumber() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisation> result = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
                                              .pageable(Pageable.unpaged())
                                              .searchCriterias(of(String.valueOf(
                                                  version3.getOrganisationNumber())))
                                              .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

}