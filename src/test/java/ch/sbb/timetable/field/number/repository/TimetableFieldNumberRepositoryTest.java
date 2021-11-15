package ch.sbb.timetable.field.number.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TimetableFieldNumberRepositoryTest {

  private static final String TTFNID = "ttfnid";
  private final VersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;

  @Autowired
  public TimetableFieldNumberRepositoryTest(VersionRepository versionRepository,
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
    Version validLastYear = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Last Year")
                                   .validFrom(LocalDate.now().minusYears(2))
                                   .validTo(LocalDate.now().minusYears(1))
                                   .build();
    versionRepository.saveAndFlush(validLastYear);

    Version validToday = Version.builder()
                                .ttfnid(TTFNID)
                                .name("Today")
                                .validFrom(LocalDate.now().minusDays(1))
                                .validTo(LocalDate.now().plusDays(1))
                                .build();
    versionRepository.saveAndFlush(validToday);

    Version validNextYear = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Next Year")
                                   .validFrom(LocalDate.now().plusYears(1))
                                   .validTo(LocalDate.now().plusYears(2))
                                   .build();
    versionRepository.saveAndFlush(validNextYear);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

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
    Version validLastYear = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Last Year")
                                   .validFrom(LocalDate.now().minusYears(2))
                                   .validTo(LocalDate.now().minusYears(1))
                                   .build();
    versionRepository.saveAndFlush(validLastYear);

    Version validNextYear = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Next Year")
                                   .validFrom(LocalDate.now().plusYears(1))
                                   .validTo(LocalDate.now().plusYears(2))
                                   .build();
    versionRepository.saveAndFlush(validNextYear);

    Version validInTwoYears = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Later")
                                   .validFrom(LocalDate.now().plusYears(3))
                                   .validTo(LocalDate.now().plusYears(4))
                                   .build();
    versionRepository.saveAndFlush(validInTwoYears);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

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
    Version validEarlier = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Earlier")
                                   .validFrom(LocalDate.now().minusYears(4))
                                   .validTo(LocalDate.now().minusYears(3))
                                   .build();
    versionRepository.saveAndFlush(validEarlier);

    Version validLastYear = Version.builder()
                                   .ttfnid(TTFNID)
                                   .name("Last Year")
                                   .validFrom(LocalDate.now().minusYears(2))
                                   .validTo(LocalDate.now().minusYears(1))
                                   .build();
    versionRepository.saveAndFlush(validLastYear);

    // When
    Page<TimetableFieldNumber> result = timetableFieldNumberRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    TimetableFieldNumber timetableFieldNumber = result.getContent().get(0);
    assertThat(timetableFieldNumber).usingRecursiveComparison()
                                    .ignoringFields("validFrom", "validTo")
                                    .isEqualTo(validLastYear);
    assertThat(timetableFieldNumber.getValidFrom()).isEqualTo(validEarlier.getValidFrom());
    assertThat(timetableFieldNumber.getValidTo()).isEqualTo(validLastYear.getValidTo());
  }

}