package ch.sbb.business.organisation.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class BusinessOrganisationRepositoryTest {

  private static final String SBOID = "sboid";
  private static final String[] IGNORED_FIELDS = {"validFrom", "validTo"};
  private final BusinessOrganisationVersionRepository versionRepository;
  private final BusinessOrganisationRepository businessOrganisationRepository;

  @Autowired
  public BusinessOrganisationRepositoryTest(BusinessOrganisationVersionRepository versionRepository,
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

  @Test
  public void shouldReturnFullLineVersions() {
    //given
    BusinessOrganisationVersion version1 =
        BusinessOrganisationData.businessOrganisationVersionBuilder()
                                .sboid(SBOID)
                                .descriptionDe("Earlier")
                                .validFrom(LocalDate.now().minusYears(4))
                                .validTo(LocalDate.now().minusYears(3))
                                .build();
    BusinessOrganisationVersion version2 =
        BusinessOrganisationData.businessOrganisationVersionBuilder()
                                .sboid("sboid2")
                                .descriptionDe("after")
                                .validFrom(LocalDate.now())
                                .validTo(LocalDate.now())
                                .build();
    versionRepository.saveAndFlush(version1);
    versionRepository.saveAndFlush(version2);
    //when
    List<BusinessOrganisationVersion> result = versionRepository.getFullLineVersions();

    //then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result).containsAnyElementsOf(result);

  }

  @Test
  public void shouldReturnActualLineVersions() {
    //given
    BusinessOrganisationVersion version1 =
        BusinessOrganisationData.businessOrganisationVersionBuilder()
                                .sboid(SBOID)
                                .descriptionDe("Earlier")
                                .validFrom(LocalDate.of(2000, 1, 1))
                                .validTo(LocalDate.of(2000, 12, 31))
                                .build();
    BusinessOrganisationVersion version2 =
        BusinessOrganisationData.businessOrganisationVersionBuilder()
                                .sboid("sboid2")
                                .descriptionDe("after")
                                .validFrom(LocalDate.of(2001, 1, 1))
                                .validTo(LocalDate.of(2001, 12, 31))
                                .build();
    versionRepository.saveAndFlush(version1);
    versionRepository.saveAndFlush(version2);
    //when
    List<BusinessOrganisationVersion> result = versionRepository.getActualLineVersions(
        LocalDate.of(2000, 6, 1));

    //then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isEqualTo(version1);

  }

}
