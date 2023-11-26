package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class TimetableFieldNumberRepositoryTest {

  private static final String TTFNID = "ttfnid";
  private final TimetableFieldNumberVersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;

  @Autowired
   TimetableFieldNumberRepositoryTest(TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberRepository timetableFieldNumberRepository) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberRepository = timetableFieldNumberRepository;
  }

  /**
   * |--Last Year--|  |--Today--|   |--Next Year--|
   */
  @Test
  void shouldDisplayNameOfCurrentDay() {
    // Given
    TimetableFieldNumberVersion validLastYear = TimetableFieldNumberVersion.builder()
                                                                           .ttfnid(TTFNID)
                                                                           .description("Last Year")
                                                                           .swissTimetableFieldNumber(
                                                                               "a.100")
                                                                           .status(Status.VALIDATED)
                                                                           .number("10.100")
                                                                           .validFrom(
                                                                               LocalDate.now()
                                                                                        .minusYears(
                                                                                            2))
                                                                           .validTo(LocalDate.now()
                                                                                             .minusYears(
                                                                                                 1))
                                                                           .businessOrganisation(
                                                                               "sbb")
                                                                           .build();
    versionRepository.saveAndFlush(validLastYear);

    TimetableFieldNumberVersion validToday = TimetableFieldNumberVersion.builder()
                                                                        .ttfnid(TTFNID)
                                                                        .description("Today")
                                                                        .swissTimetableFieldNumber(
                                                                            "a.100")
                                                                        .status(Status.VALIDATED)
                                                                        .number("10.100")
                                                                        .validFrom(LocalDate.now()
                                                                                            .minusDays(
                                                                                                1))
                                                                        .validTo(LocalDate.now()
                                                                                          .plusDays(
                                                                                              1))
                                                                        .businessOrganisation("sbb")
                                                                        .build();
    versionRepository.saveAndFlush(validToday);

    TimetableFieldNumberVersion validNextYear = TimetableFieldNumberVersion.builder()
                                                                           .ttfnid(TTFNID)
                                                                           .description("Next Year")
                                                                           .swissTimetableFieldNumber(
                                                                               "a.100")
                                                                           .status(Status.VALIDATED)
                                                                           .number("10.100")
                                                                           .validFrom(
                                                                               LocalDate.now()
                                                                                        .plusYears(
                                                                                            1))
                                                                           .validTo(LocalDate.now()
                                                                                             .plusYears(
                                                                                                 2))
                                                                           .businessOrganisation(
                                                                               "sbb")
                                                                           .build();
    versionRepository.saveAndFlush(validNextYear);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    TimetableFieldNumber timetableFieldNumber = result.getContent().get(0);
    assertThat(timetableFieldNumber).usingRecursiveComparison()
                                    .ignoringFields("validFrom", "validTo")
                                    .isEqualTo(validToday);
    assertThat(timetableFieldNumber.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(timetableFieldNumber.getValidTo()).isEqualTo(validNextYear.getValidTo());
  }

  /**
   * |--Last Year--|  |--Next Year--| |--Later--|
   */
  @Test
  void shouldDisplayNameOfNextYear() {
    // Given
    TimetableFieldNumberVersion validLastYear = TimetableFieldNumberVersion.builder()
                                                                           .ttfnid(TTFNID)
                                                                           .description("Last Year")
                                                                           .swissTimetableFieldNumber(
                                                                               "a.100")
                                                                           .status(Status.VALIDATED)
                                                                           .number("10.100")
                                                                           .validFrom(
                                                                               LocalDate.now()
                                                                                        .minusYears(
                                                                                            2))
                                                                           .validTo(LocalDate.now()
                                                                                             .minusYears(
                                                                                                 1))
                                                                           .businessOrganisation(
                                                                               "sbb")
                                                                           .build();
    versionRepository.saveAndFlush(validLastYear);

    TimetableFieldNumberVersion validNextYear = TimetableFieldNumberVersion.builder()
                                                                           .ttfnid(TTFNID)
                                                                           .description("Next Year")
                                                                           .swissTimetableFieldNumber(
                                                                               "a.100")
                                                                           .status(Status.VALIDATED)
                                                                           .number("10.100")
                                                                           .validFrom(
                                                                               LocalDate.now()
                                                                                        .plusYears(
                                                                                            1))
                                                                           .validTo(LocalDate.now()
                                                                                             .plusYears(
                                                                                                 2))
                                                                           .businessOrganisation(
                                                                               "sbb")
                                                                           .build();
    versionRepository.saveAndFlush(validNextYear);

    TimetableFieldNumberVersion validInTwoYears = TimetableFieldNumberVersion.builder()
                                                                             .ttfnid(TTFNID)
                                                                             .description("Later")
                                                                             .swissTimetableFieldNumber(
                                                                                 "a.100")
                                                                             .status(Status.VALIDATED)
                                                                             .number("10.100")
                                                                             .validFrom(
                                                                                 LocalDate.now()
                                                                                          .plusYears(
                                                                                              3))
                                                                             .validTo(
                                                                                 LocalDate.now()
                                                                                          .plusYears(
                                                                                              4))
                                                                             .businessOrganisation(
                                                                                 "sbb")
                                                                             .build();
    versionRepository.saveAndFlush(validInTwoYears);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    TimetableFieldNumber timetableFieldNumber = result.getContent().get(0);
    assertThat(timetableFieldNumber).usingRecursiveComparison()
                                    .ignoringFields("validFrom", "validTo")
                                    .isEqualTo(validNextYear);
    assertThat(timetableFieldNumber.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(timetableFieldNumber.getValidTo()).isEqualTo(validInTwoYears.getValidTo());
  }

  /**
   * |--Earlier--| |--Last Year--|
   */
  @Test
  void shouldDisplayNameOfLastYear() {
    // Given
    TimetableFieldNumberVersion validEarlier = TimetableFieldNumberVersion.builder()
                                                                          .ttfnid(TTFNID)
                                                                          .description("Earlier")
                                                                          .swissTimetableFieldNumber(
                                                                              "a.100")
                                                                          .status(Status.VALIDATED)
                                                                          .number("10.100")
                                                                          .validFrom(LocalDate.now()
                                                                                              .minusYears(
                                                                                                  4))
                                                                          .validTo(LocalDate.now()
                                                                                            .minusYears(
                                                                                                3))
                                                                          .businessOrganisation(
                                                                              "sbb")
                                                                          .build();
    versionRepository.saveAndFlush(validEarlier);

    TimetableFieldNumberVersion validLastYear = TimetableFieldNumberVersion.builder()
                                                                           .ttfnid(TTFNID)
                                                                           .description("Last Year")
                                                                           .swissTimetableFieldNumber(
                                                                               "a.100")
                                                                           .status(Status.VALIDATED)
                                                                           .number("10.100")
                                                                           .validFrom(
                                                                               LocalDate.now()
                                                                                        .minusYears(
                                                                                            2))
                                                                           .validTo(LocalDate.now()
                                                                                             .minusYears(
                                                                                                 1))
                                                                           .businessOrganisation(
                                                                               "sbb")
                                                                           .build();
    versionRepository.saveAndFlush(validLastYear);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent()).hasSize(1);

    TimetableFieldNumber timetableFieldNumber = result.getContent().get(0);
    assertThat(timetableFieldNumber).usingRecursiveComparison()
                                    .ignoringFields("validFrom", "validTo")
                                    .isEqualTo(validLastYear);
    assertThat(timetableFieldNumber.getValidFrom()).isEqualTo(validEarlier.getValidFrom());
    assertThat(timetableFieldNumber.getValidTo()).isEqualTo(validLastYear.getValidTo());
  }


  @Test
  void shouldGetFullLineVersions() {
    //given
    TimetableFieldNumberVersion fieldNumberVersion =
        TimetableFieldNumberVersion.builder()
                                   .ttfnid(TTFNID)
                                   .description("Earlier")
                                   .swissTimetableFieldNumber("a.100")
                                   .status(Status.VALIDATED)
                                   .number("10.100")
                                   .validFrom(LocalDate.now().minusYears(4))
                                   .validTo(LocalDate.now().minusYears(3))
                                   .businessOrganisation("sbb")
                                   .build();
    versionRepository.saveAndFlush(fieldNumberVersion);
    //when
    List<TimetableFieldNumberVersion> result = versionRepository.getFullTimeTableNumberVersions();

    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldGetActualLineVersions() {
    //given
    TimetableFieldNumberVersion fieldNumberVersion =
        TimetableFieldNumberVersion.builder()
                                   .ttfnid(TTFNID)
                                   .description("Earlier")
                                   .swissTimetableFieldNumber("a.100")
                                   .status(Status.VALIDATED)
                                   .number("10.100")
                                   .validFrom(LocalDate.of(2022, 1, 1))
                                   .validTo(LocalDate.of(2022, 1, 1))
                                   .businessOrganisation("sbb")
                                   .build();
    versionRepository.saveAndFlush(fieldNumberVersion);
    //when
    List<TimetableFieldNumberVersion> result = versionRepository.getActualTimeTableNumberVersions(
        LocalDate.of(2022, 1, 1));

    //then
    assertThat(result).hasSize(1);
    assertThat(result).contains(fieldNumberVersion);

  }

}
