package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class LineRepositoryTest {

  private static final String SLNID = "slnid";
  private final LineVersionRepository lineVersionRepository;
  private final LineRepository lineRepository;

  @Autowired
   LineRepositoryTest(LineVersionRepository lineVersionRepository,
      LineRepository lineRepository) {
    this.lineVersionRepository = lineVersionRepository;
    this.lineRepository = lineRepository;
  }

  /**
   * |--Last Year--|  |--Today--|   |--Next Year--|
   */
  @Test
  void shouldDisplayDescriptionOfCurrentDay() {
    // Given
    LineVersion validLastYear = LineTestData.lineVersionBuilder()
                                            .slnid(SLNID)
                                            .description("Last Year")
                                            .validFrom(LocalDate.now().minusYears(2))
                                            .validTo(LocalDate.now().minusYears(1))
                                            .build();
    lineVersionRepository.saveAndFlush(validLastYear);

    LineVersion validToday = LineTestData.lineVersionBuilder()
                                         .slnid(SLNID)
                                         .description("Today")
                                         .validFrom(LocalDate.now().minusDays(1))
                                         .validTo(LocalDate.now().plusDays(1))
                                         .build();
    lineVersionRepository.saveAndFlush(validToday);

    LineVersion validNextYear = LineTestData.lineVersionBuilder()
                                            .slnid(SLNID)
                                            .description("Next Year")
                                            .validFrom(LocalDate.now().plusYears(1))
                                            .validTo(LocalDate.now().plusYears(2))
                                            .build();
    lineVersionRepository.saveAndFlush(validNextYear);

    // When
    Page<Line> result = lineRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    Line line = result.getContent().get(0);
    assertThat(line).usingRecursiveComparison()
                    .ignoringFields("validFrom", "validTo")
                    .isEqualTo(validToday);
    assertThat(line.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(line.getValidTo()).isEqualTo(validNextYear.getValidTo());
  }

  /**
   * |--Last Year--|  |--Next Year--| |--Later--|
   */
  @Test
  void shouldDisplayDescriptionOfNextYear() {
    // Given
    LineVersion validLastYear = LineTestData.lineVersionBuilder()
                                            .slnid(SLNID)
                                            .description("Last Year")
                                            .validFrom(LocalDate.now().minusYears(2))
                                            .validTo(LocalDate.now().minusYears(1))
                                            .build();
    lineVersionRepository.saveAndFlush(validLastYear);

    LineVersion validNextYear = LineTestData.lineVersionBuilder()
                                            .slnid(SLNID)
                                            .description("Next Year")
                                            .validFrom(LocalDate.now().plusYears(1))
                                            .validTo(LocalDate.now().plusYears(2))
                                            .build();
    lineVersionRepository.saveAndFlush(validNextYear);

    LineVersion validInTwoYears = LineTestData.lineVersionBuilder()
                                              .slnid(SLNID)
                                              .description("Later")
                                              .validFrom(LocalDate.now().plusYears(3))
                                              .validTo(LocalDate.now().plusYears(4))
                                              .build();
    lineVersionRepository.saveAndFlush(validInTwoYears);

    // When
    Page<Line> result = lineRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    Line line = result.getContent().get(0);
    assertThat(line).usingRecursiveComparison()
                    .ignoringFields("validFrom", "validTo")
                    .isEqualTo(validNextYear);
    assertThat(line.getValidFrom()).isEqualTo(validLastYear.getValidFrom());
    assertThat(line.getValidTo()).isEqualTo(validInTwoYears.getValidTo());
  }

  /**
   * |--Earlier--| |--Last Year--|
   */
  @Test
  void shouldDisplayDescriptionOfLastYear() {
    // Given
    LineVersion validEarlier = LineTestData.lineVersionBuilder()
                                           .slnid(SLNID)
                                           .description("Earlier")
                                           .validFrom(LocalDate.now().minusYears(4))
                                           .validTo(LocalDate.now().minusYears(3))
                                           .build();
    lineVersionRepository.saveAndFlush(validEarlier);

    LineVersion validLastYear = LineTestData.lineVersionBuilder()
                                            .slnid(SLNID)
                                            .description("Last Year")
                                            .validFrom(LocalDate.now().minusYears(2))
                                            .validTo(LocalDate.now().minusYears(1))
                                            .build();
    lineVersionRepository.saveAndFlush(validLastYear);

    // When
    Page<Line> result = lineRepository.findAll(Pageable.unpaged());

    // Then
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getContent().size()).isEqualTo(1L);

    Line line = result.getContent().get(0);
    assertThat(line).usingRecursiveComparison()
                    .ignoringFields("validFrom", "validTo")
                    .isEqualTo(validLastYear);
    assertThat(line.getValidFrom()).isEqualTo(validEarlier.getValidFrom());
    assertThat(line.getValidTo()).isEqualTo(validLastYear.getValidTo());
  }

  @Test
  void shouldDeleteLines() {
    // Given
    LineVersion validEarlier = LineTestData.lineVersionBuilder()
                                           .slnid(SLNID)
                                           .description("Earlier")
                                           .validFrom(LocalDate.now().minusYears(4))
                                           .validTo(LocalDate.now().minusYears(3))
                                           .build();
    lineVersionRepository.saveAndFlush(validEarlier);

    LineVersion validLastYear = LineTestData.lineVersionBuilder()
                                            .slnid(SLNID)
                                            .description("Last Year")
                                            .validFrom(LocalDate.now().minusYears(2))
                                            .validTo(LocalDate.now().minusYears(1))
                                            .build();
    lineVersionRepository.saveAndFlush(validLastYear);

    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(SLNID);
    assertThat(lineVersions.size()).isEqualTo(2);

    // When
    lineVersionRepository.deleteAll(lineVersions);

    // Then
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(SLNID);
    assertThat(result.size()).isEqualTo(0);

  }

}