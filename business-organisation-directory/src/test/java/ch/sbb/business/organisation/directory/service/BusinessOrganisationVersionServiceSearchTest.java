package ch.sbb.business.organisation.directory.service;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionRequestParams;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.controller.BusinessOrganisationVersionSearchRestrictions;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class BusinessOrganisationVersionServiceSearchTest {

  private final BusinessOrganisationVersionRepository repository;
  private final BusinessOrganisationService service;

  private BusinessOrganisationVersion version1;
  private BusinessOrganisationVersion version2;
  private BusinessOrganisationVersion version3;

  @Autowired
   BusinessOrganisationVersionServiceSearchTest(BusinessOrganisationVersionRepository repository,
      BusinessOrganisationService service) {
    this.repository = repository;
    this.service = service;
  }

  @BeforeEach
   void init() {
    version1 = BusinessOrganisationVersion.builder().sboid("ch:1:sboid:100000").abbreviationDe("de1").abbreviationFr("fr1")
        .abbreviationIt("it1").abbreviationEn("en1").descriptionDe("desc-de1").descriptionFr("desc-fr1").descriptionIt("desc-it1")
        .descriptionEn("desc-en1")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail1@mail.ch").organisationNumber(1234).status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1)).validTo(LocalDate.of(2021, 12, 31)).build();
    version2 = BusinessOrganisationVersion.builder().sboid("ch:1:sboid:100001").abbreviationDe("de2").abbreviationFr("fr2")
        .abbreviationIt("it2").abbreviationEn("en2").descriptionDe("desc-de2").descriptionFr("desc-fr2").descriptionIt("desc-it2")
        .descriptionEn("desc-en2")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail1@mail.ch").organisationNumber(12345).status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1)).validTo(LocalDate.of(2023, 12, 31)).build();
    version3 = BusinessOrganisationVersion.builder().sboid("ch:1:sboid:100003").abbreviationDe("de3").abbreviationFr("fr3")
        .abbreviationIt("it3").abbreviationEn("en3").descriptionDe("desc-de3").descriptionFr("desc-fr3").descriptionIt("desc-it3")
        .descriptionEn("desc-en3")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail1@mail.ch").organisationNumber(12346).status(Status.VALIDATED)
        .validFrom(LocalDate.of(2024, 1, 1)).validTo(LocalDate.of(2025, 12, 31)).build();
  }

  @AfterEach
   void cleanUp() {
    repository.deleteAll();
  }

  @Test
  void shouldFindVersionWithValidOn() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().validOn(LocalDate.of(2020, 1, 1)).build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithSboidsIn() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().inSboids(List.of(version1.getSboid())).build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionsWithSboidsIn() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().inSboids(List.of(version1.getSboid(), version2.getSboid()))
                    .build()).build());

    //then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldNotFindVersionWithValidOn() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().validOn(LocalDate.of(2019, 1, 1)).build()).build());

    //then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldgetBusinessOrganisationVersionsVersionOnNoRestrictions() {
    //given
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(BusinessOrganisationVersionRequestParams.builder().build())
            .build());

    //then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithText() {
    //given
    repository.saveAndFlush(version1);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().searchCriteria(of("de1")).build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithStatus() {
    //given
    version1.setDescriptionDe("Forza Napoli sempre");
    repository.saveAndFlush(version1);
    repository.saveAndFlush(version2);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().searchCriteria(of("1", "Napoli", "Forza"))
                    .statusChoices(List.of(Status.VALIDATED)).build()).build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithMultipleStatus() {
    //given
    repository.saveAndFlush(version1);
    version2.setStatus(Status.REVOKED);
    repository.saveAndFlush(version2);
    version3.setStatus(Status.WITHDRAWN);
    repository.saveAndFlush(version3);
    //when
    Page<BusinessOrganisationVersion> result = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().statusChoices(of(Status.WITHDRAWN, Status.REVOKED)).build())

            .build());

    //then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldFindVersion1ByWithFromDate() {
    version1 = repository.saveAndFlush(version1);
    LocalDate fromDate = version1.getValidFrom().minusDays(1);
    assertThat(fromDate).isBefore(version1.getValidFrom());

    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().fromDate(fromDate).build()).build());
    assertThat(businessOrganisations.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindVersion1ByWithFromDate() {
    version1 = repository.saveAndFlush(version1);
    LocalDate fromDate = version1.getValidFrom().plusDays(1);
    assertThat(fromDate).isAfter(version1.getValidFrom());

    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().fromDate(fromDate).build()).build());
    assertThat(businessOrganisations.getTotalElements()).isZero();
  }

  @Test
  void shouldFindVersion1ByWithToDate() {
    version1 = repository.saveAndFlush(version1);
    LocalDate toDate = version1.getValidTo().plusDays(1);
    assertThat(toDate).isAfter(version1.getValidTo());

    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(BusinessOrganisationVersionRequestParams.builder().toDate(toDate).build())
            .build());
    assertThat(businessOrganisations.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindVersion1ByWithToDate() {
    version1 = repository.saveAndFlush(version1);
    LocalDate toDate = version1.getValidTo().minusDays(1);
    assertThat(toDate).isBefore(version1.getValidTo());

    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(BusinessOrganisationVersionRequestParams.builder().toDate(toDate).build())
            .build());
    assertThat(businessOrganisations.getTotalElements()).isZero();
  }

  @Test
  void shouldFindVersion1ByWithCreatedAfter() {
    version1 = repository.saveAndFlush(version1);
    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().createdAfter(version1.getCreationDate().minusSeconds(1))
                    .build()).build());
    assertThat(businessOrganisations.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindVersion1ByWithCreatedAfter() {
    version1 = repository.saveAndFlush(version1);
    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().createdAfter(version1.getCreationDate().plusSeconds(1))
                    .build()).build());
    assertThat(businessOrganisations.getTotalElements()).isZero();
  }

  @Test
  void shouldFindVersion1ByWithModifiedAfter() {
    version1 = repository.saveAndFlush(version1);
    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().modifiedAfter(version1.getEditionDate().minusSeconds(1))
                    .build()).build());
    assertThat(businessOrganisations.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindVersion1ByWithModifiedAfter() {
    version1 = repository.saveAndFlush(version1);
    Page<BusinessOrganisationVersion> businessOrganisations = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder().pageable(Pageable.unpaged())
            .businessOrganisationVersionRequestParams(
                BusinessOrganisationVersionRequestParams.builder().modifiedAfter(version1.getEditionDate().plusSeconds(1))
                    .build()).build());
    assertThat(businessOrganisations.getTotalElements()).isZero();
  }
}