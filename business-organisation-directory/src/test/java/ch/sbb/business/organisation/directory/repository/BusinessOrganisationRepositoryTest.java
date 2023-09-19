package ch.sbb.business.organisation.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class BusinessOrganisationRepositoryTest {

  private static final String SBOID = "sboid";
  private static final String[] IGNORED_FIELDS = {"validFrom", "validTo"};
  private final BusinessOrganisationVersionRepository versionRepository;
  private final BusinessOrganisationRepository businessOrganisationRepository;

  @Autowired
   BusinessOrganisationRepositoryTest(BusinessOrganisationVersionRepository versionRepository,
      BusinessOrganisationRepository businessOrganisationRepository) {
    this.versionRepository = versionRepository;
    this.businessOrganisationRepository = businessOrganisationRepository;
  }

  /**
   * |--Last Year--|  |--Today--|   |--Next Year--|
   */
  @Test
  void shouldDisplayNameOfCurrentDay() {
    // Given
    BusinessOrganisationVersion validLastYear = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Last Year")
                                                                        .validFrom(LocalDate.now()
                                                                                            .minusYears(
                                                                                                2))
                                                                        .validTo(LocalDate.now()
                                                                                          .minusYears(
                                                                                              1))
                                                                        .build();
    versionRepository.saveAndFlush(validLastYear);

    BusinessOrganisationVersion validToday = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                     .sboid(SBOID)
                                                                     .descriptionDe("Today")
                                                                     .validFrom(LocalDate.now()
                                                                                         .minusDays(
                                                                                             1))
                                                                     .validTo(LocalDate.now()
                                                                                       .plusDays(1))
                                                                     .build();
    versionRepository.saveAndFlush(validToday);

    BusinessOrganisationVersion validNextYear = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Next Year")
                                                                        .validFrom(LocalDate.now()
                                                                                            .plusYears(
                                                                                                1))
                                                                        .validTo(LocalDate.now()
                                                                                          .plusYears(
                                                                                              2))
                                                                        .build();
    versionRepository.saveAndFlush(validNextYear);

    // When
    Page<BusinessOrganisation> result = businessOrganisationRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    BusinessOrganisation businessOrganisation = result.getContent().get(0);
    assertThat(businessOrganisation).usingRecursiveComparison()
                                    .ignoringFields(IGNORED_FIELDS)
                                    .isEqualTo(validToday);
    assertThat(businessOrganisation.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(businessOrganisation.getValidTo()).isEqualTo(validNextYear.getValidTo());
  }

  /**
   * |--Last Year--|  |--Next Year--| |--Later--|
   */
  @Test
  void shouldDisplayNameOfNextYear() {
    // Given
    BusinessOrganisationVersion validLastYear = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Last Year")
                                                                        .validFrom(LocalDate.now()
                                                                                            .minusYears(
                                                                                                2))
                                                                        .validTo(LocalDate.now()
                                                                                          .minusYears(
                                                                                              1))
                                                                        .build();
    versionRepository.saveAndFlush(validLastYear);

    BusinessOrganisationVersion validNextYear = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Next Year")
                                                                        .validFrom(LocalDate.now()
                                                                                            .plusYears(
                                                                                                1))
                                                                        .validTo(LocalDate.now()
                                                                                          .plusYears(
                                                                                              2))
                                                                        .build();
    versionRepository.saveAndFlush(validNextYear);

    BusinessOrganisationVersion validInTwoYears = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                          .sboid(SBOID)
                                                                          .descriptionDe("Later")
                                                                          .validFrom(LocalDate.now()
                                                                                              .plusYears(
                                                                                                  3))
                                                                          .validTo(LocalDate.now()
                                                                                            .plusYears(
                                                                                                4))
                                                                          .build();
    versionRepository.saveAndFlush(validInTwoYears);

    // When
    Page<BusinessOrganisation> result = businessOrganisationRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    BusinessOrganisation businessOrganisation = result.getContent().get(0);
    assertThat(businessOrganisation).usingRecursiveComparison()
                                    .ignoringFields(IGNORED_FIELDS)
                                    .isEqualTo(validNextYear);
    assertThat(businessOrganisation.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(businessOrganisation.getValidTo()).isEqualTo(validInTwoYears.getValidTo());
  }

  /**
   * |--Earlier--| |--Last Year--|
   */
  @Test
  void shouldDisplayNameOfLastYear() {
    // Given
    BusinessOrganisationVersion validEarlier = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                       .sboid(SBOID)
                                                                       .descriptionDe("Earlier")
                                                                       .validFrom(LocalDate.now()
                                                                                           .minusYears(
                                                                                               4))
                                                                       .validTo(LocalDate.now()
                                                                                         .minusYears(
                                                                                             3))
                                                                       .build();
    versionRepository.saveAndFlush(validEarlier);

    BusinessOrganisationVersion validLastYear = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Last Year")
                                                                        .validFrom(LocalDate.now()
                                                                                            .minusYears(
                                                                                                2))
                                                                        .validTo(LocalDate.now()
                                                                                          .minusYears(
                                                                                              1))
                                                                        .build();
    versionRepository.saveAndFlush(validLastYear);

    // When
    Page<BusinessOrganisation> result = businessOrganisationRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    BusinessOrganisation businessOrganisation = result.getContent().get(0);
    assertThat(businessOrganisation).usingRecursiveComparison()
                                    .ignoringFields(IGNORED_FIELDS)
                                    .isEqualTo(validLastYear);
    assertThat(businessOrganisation.getValidFrom()).isEqualTo(validEarlier.getValidFrom());
    assertThat(businessOrganisation.getValidTo()).isEqualTo(validLastYear.getValidTo());
  }

  /**
   * ATLAS-922:
   * |--Today--||--Tomorrow--|
   */
  @Test
  void shouldDisplayNameOfCurrentDayWhenThereIsTomorrow() {
    // Given
    BusinessOrganisationVersion validToday = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                     .sboid(SBOID)
                                                                     .descriptionDe("Today")
                                                                     .validFrom(LocalDate.now())
                                                                     .validTo(LocalDate.now())
                                                                     .build();
    versionRepository.saveAndFlush(validToday);

    BusinessOrganisationVersion validTomorrow = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Tomorrow")
                                                                        .validFrom(LocalDate.now().plusDays(1))
                                                                        .validTo(LocalDate.now().plusDays(1))
                                                                        .build();
    versionRepository.saveAndFlush(validTomorrow);

    // When
    Page<BusinessOrganisation> result = businessOrganisationRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    BusinessOrganisation businessOrganisation = result.getContent().get(0);
    assertThat(businessOrganisation).usingRecursiveComparison()
                                    .ignoringFields(IGNORED_FIELDS)
                                    .isEqualTo(validToday);
  }

  /**
   * ATLAS-922:
   * |--Today+Tomorrow--||--Later--|
   */
  @Test
  void shouldDisplayNameOfCurrentVersion() {
    // Given
    BusinessOrganisationVersion validToday = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                     .sboid(SBOID)
                                                                     .descriptionDe("Today+Tomorrow")
                                                                     .validFrom(LocalDate.now())
                                                                     .validTo(LocalDate.now().plusDays(1))
                                                                     .build();
    versionRepository.saveAndFlush(validToday);

    BusinessOrganisationVersion validLater = BusinessOrganisationData.businessOrganisationVersionBuilder()
                                                                        .sboid(SBOID)
                                                                        .descriptionDe("Later")
                                                                        .validFrom(LocalDate.now().plusDays(2))
                                                                        .validTo(LocalDate.now().plusDays(2))
                                                                        .build();
    versionRepository.saveAndFlush(validLater);

    // When
    Page<BusinessOrganisation> result = businessOrganisationRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    BusinessOrganisation businessOrganisation = result.getContent().get(0);
    assertThat(businessOrganisation).usingRecursiveComparison()
                                    .ignoringFields(IGNORED_FIELDS)
                                    .isEqualTo(validToday);
  }

}
