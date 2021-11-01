package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LineVersionRepositoryTest {

  private static final LineVersion LINE_VERSION = LineTestData.lineVersion();

  private final LineVersionRepository lineVersionRepository;

  @Autowired
  public LineVersionRepositoryTest(LineVersionRepository lineVersionRepository) {
    this.lineVersionRepository = lineVersionRepository;
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    lineVersionRepository.save(LINE_VERSION);

    //when
    LineVersion result = lineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison()
                      .ignoringActualNullFields()
                      .isEqualTo(LINE_VERSION);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");
    assertThat(result.getCreationDate()).isNotNull();
    assertThat(result.getEditionDate()).isNotNull();
  }

  @Test
  void shouldUpdateSimpleLineVersion() {
    //given
    LineVersion result = lineVersionRepository.save(LINE_VERSION);

    //when
    result.setNumber("other number");
    result = lineVersionRepository.save(result);

    //then
    assertThat(result.getNumber()).isEqualTo("other number");
  }

  @Test
  void shouldGetCountVersions() {
    //given
    lineVersionRepository.save(LINE_VERSION);

    //when
    long result = lineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    LineVersion lineVersion = lineVersionRepository.save(LINE_VERSION);
    lineVersionRepository.delete(lineVersion);

    //when
    List<LineVersion> result = lineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  /**
   * New:                  |_________1___________|
   * Current: |-----1-----|                       |-----------1---------|
   */
  @Test
  void shouldAllowSwissNumberOnDifferentSwissIds() {
    // Given
    lineVersionRepository.save(LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2019, 1, 1))
                                           .validTo(LocalDate.of(2019, 12, 31))
                                           .build());
    lineVersionRepository.save(LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2021, 1, 1))
                                           .validTo(LocalDate.of(2021, 12, 31))
                                           .build());
    // When
    assertThat(lineVersionRepository.hasUniqueSwissLineNumber(LINE_VERSION)).isTrue();

    // Then
  }

  /**
   * New:           |____1____|
   * Current:   |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBetween() {
    // Given
    lineVersionRepository.save(LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2019, 1, 1))
                                           .validTo(LocalDate.of(2099, 12, 31))
                                           .build());
    // When
    assertThat(lineVersionRepository.hasUniqueSwissLineNumber(LINE_VERSION)).isFalse();

    // Then
  }

  /**
   * New:         |____1____|
   * Current:         |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBeginning() {
    // Given
    lineVersionRepository.save(
        LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2020, 10, 1))
                    .validTo(LocalDate.of(2099, 12, 31))
                    .build());
    // When
    assertThat(lineVersionRepository.hasUniqueSwissLineNumber(LINE_VERSION)).isFalse();

    // Then
  }

  /**
   * New:                   |____1____|
   * Current: |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapEnd() {
    // Given
    lineVersionRepository.save(LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2000, 1, 1))
                                           .validTo(LocalDate.of(2020, 10, 31))
                                           .build());
    // When
    assertThat(lineVersionRepository.hasUniqueSwissLineNumber(LINE_VERSION)).isFalse();

    // Then
  }

  /**
   * New:     |____1____|
   * Current: |----1----|
   */
  @Test
  void shouldAllowUpdateOnSameLineVersion() {
    // Given
    LineVersion entity = lineVersionRepository.save(LINE_VERSION);
    // When
    assertThat(lineVersionRepository.hasUniqueSwissLineNumber(entity)).isTrue();

    // Then
  }

}