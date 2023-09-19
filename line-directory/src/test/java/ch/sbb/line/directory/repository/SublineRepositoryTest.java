package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class SublineRepositoryTest {

  private static final String SLNID = "slnid";
  private final SublineVersionRepository sublineVersionRepository;
  private final SublineRepository sublineRepository;
  private final LineVersionRepository lineVersionRepository;

  @Autowired
   SublineRepositoryTest(SublineVersionRepository sublineVersionRepository,
      SublineRepository sublineRepository,
      LineVersionRepository lineVersionRepository) {
    this.sublineVersionRepository = sublineVersionRepository;
    this.sublineRepository = sublineRepository;
    this.lineVersionRepository = lineVersionRepository;
  }

  /**
   * |--Last Year--|  |--Today--|   |--Next Year--|
   */
  @Test
  void shouldDisplaydescriptionOfCurrentDay() {
    // Given
    lineVersionRepository.saveAndFlush(
        LineTestData.lineVersionBuilder().slnid(SublineTestData.MAINLINE_SLNID).build());
    SublineVersion validLastYear = SublineTestData.sublineVersionBuilder()
                                                  .slnid(SLNID)
                                                  .description("Last Year")
                                                  .validFrom(LocalDate.now().minusYears(2))
                                                  .validTo(LocalDate.now().minusYears(1))
                                                  .build();
    sublineVersionRepository.saveAndFlush(validLastYear);

    SublineVersion validToday = SublineTestData.sublineVersionBuilder()
                                               .slnid(SLNID)
                                               .description("Today")
                                               .validFrom(LocalDate.now().minusDays(1))
                                               .validTo(LocalDate.now().plusDays(1))
                                               .build();
    sublineVersionRepository.saveAndFlush(validToday);

    SublineVersion validNextYear = SublineTestData.sublineVersionBuilder()
                                                  .slnid(SLNID)
                                                  .description("Next Year")
                                                  .validFrom(LocalDate.now().plusYears(1))
                                                  .validTo(LocalDate.now().plusYears(2))
                                                  .build();
    sublineVersionRepository.saveAndFlush(validNextYear);

    // When
    Page<Subline> result = sublineRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    Subline subline = result.getContent().get(0);
    assertThat(subline).usingRecursiveComparison()
                       .ignoringFields("validFrom", "validTo", "swissLineNumber")
                       .isEqualTo(validToday);
    assertThat(subline.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(subline.getValidTo()).isEqualTo(validNextYear.getValidTo());
  }

  /**
   * |--Last Year--|  |--Next Year--| |--Later--|
   */
  @Test
  void shouldDisplaydescriptionOfNextYear() {
    // Given
    lineVersionRepository.saveAndFlush(
        LineTestData.lineVersionBuilder().slnid(SublineTestData.MAINLINE_SLNID).build());
    SublineVersion validLastYear = SublineTestData.sublineVersionBuilder()
                                                  .slnid(SLNID)
                                                  .description("Last Year")
                                                  .validFrom(LocalDate.now().minusYears(2))
                                                  .validTo(LocalDate.now().minusYears(1))
                                                  .build();
    sublineVersionRepository.saveAndFlush(validLastYear);

    SublineVersion validNextYear = SublineTestData.sublineVersionBuilder()
                                                  .slnid(SLNID)
                                                  .description("Next Year")
                                                  .validFrom(LocalDate.now().plusYears(1))
                                                  .validTo(LocalDate.now().plusYears(2))
                                                  .build();
    sublineVersionRepository.saveAndFlush(validNextYear);

    SublineVersion validInTwoYears = SublineTestData.sublineVersionBuilder()
                                                    .slnid(SLNID)
                                                    .description("Later")
                                                    .validFrom(LocalDate.now().plusYears(3))
                                                    .validTo(LocalDate.now().plusYears(4))
                                                    .build();
    sublineVersionRepository.saveAndFlush(validInTwoYears);

    // When
    Page<Subline> result = sublineRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    Subline subline = result.getContent().get(0);
    assertThat(subline).usingRecursiveComparison()
                       .ignoringFields("validFrom", "validTo", "swissLineNumber")
                       .isEqualTo(validNextYear);
    assertThat(subline.getSwissLineNumber()).isEqualTo("swissLineNumber");
    assertThat(subline.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(subline.getValidTo()).isEqualTo(validInTwoYears.getValidTo());
  }

  /**
   * |--Earlier--| |--Last Year--|
   */
  @Test
  void shouldDisplaydescriptionOfLastYear() {
    // Given
    lineVersionRepository.saveAndFlush(
        LineTestData.lineVersionBuilder().slnid(SublineTestData.MAINLINE_SLNID).build());
    SublineVersion validEarlier = SublineTestData.sublineVersionBuilder()
                                                 .slnid(SLNID)
                                                 .description("Earlier")
                                                 .validFrom(LocalDate.now().minusYears(4))
                                                 .validTo(LocalDate.now().minusYears(3))
                                                 .build();
    sublineVersionRepository.saveAndFlush(validEarlier);

    SublineVersion validLastYear = SublineTestData.sublineVersionBuilder()
                                                  .slnid(SLNID)
                                                  .description("Last Year")
                                                  .validFrom(LocalDate.now().minusYears(2))
                                                  .validTo(LocalDate.now().minusYears(1))
                                                  .build();
    sublineVersionRepository.saveAndFlush(validLastYear);

    // When
    Page<Subline> result = sublineRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    Subline subline = result.getContent().get(0);
    assertThat(subline).usingRecursiveComparison()
                       .ignoringFields("validFrom", "validTo", "swissLineNumber")
                       .isEqualTo(validLastYear);
    assertThat(subline.getValidFrom()).isEqualTo(validEarlier.getValidFrom());
    assertThat(subline.getValidTo()).isEqualTo(validLastYear.getValidTo());
  }

}