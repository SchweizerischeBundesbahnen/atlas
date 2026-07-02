package ch.sbb.line.directory.module.line.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.module.line.LineTestData;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class LineVersionRepositoryTest {

  private static final LineVersion LINE_VERSION = LineTestData.lineVersion();

  private final LineVersionRepository lineVersionRepository;

  @Autowired
  LineVersionRepositoryTest(LineVersionRepository lineVersionRepository) {
    this.lineVersionRepository = lineVersionRepository;
  }

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    LineVersion savedLine = lineVersionRepository.save(LineTestData.lineVersion());

    //when
    LineVersion result = lineVersionRepository.findAll().getFirst();

    //then
    assertThat(result).usingRecursiveComparison()
        .ignoringActualNullFields()
        .isEqualTo(savedLine);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");

    assertThat(result.getCreationDate()).isNotNull();
    assertThat(result.getEditionDate()).isNotNull();

    assertThat(result.getCreator()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
    assertThat(result.getEditor()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
  }

  @Test
  void shouldUpdateSimpleLineVersion() {
    //given
    LineVersion result = lineVersionRepository.save(LineTestData.lineVersion());

    //when
    result.setNumber("other");
    result = lineVersionRepository.save(result);

    //then
    assertThat(result.getNumber()).isEqualTo("other");
  }

  @Test
  void shouldGetCountVersions() {
    //given
    lineVersionRepository.save(LineTestData.lineVersion());

    //when
    long result = lineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    LineVersion lineVersion = lineVersionRepository.save(LineTestData.lineVersion());
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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION)).isEmpty();

    // Then
  }

  /**
   * New:           |____1____|
   * Current:   |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBetween() {
    // Given
    lineVersionRepository.save(LineTestData.lineVersionBuilder()
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .swissLineNumber("SWISSLineNUMBER")
        .build());
    // When
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION)).isNotEmpty();

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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION)).isNotEmpty();

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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION)).isNotEmpty();

    // Then
  }

  /**
   * New:     |____1____|
   * Current: |----1----|
   */
  @Test
  void shouldAllowUpdateOnSameLineVersion() {
    // Given
    LineVersion entity = lineVersionRepository.save(LineTestData.lineVersion());
    // When
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(entity)).isEmpty();

    // Then
  }

  @Test
  void shouldAllowRevokedSwissNumberOnOverlapBetween() {
    // Given
    lineVersionRepository.save(LineTestData.lineVersionBuilder()
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .swissLineNumber("SWISSLineNUMBER")
        .status(Status.REVOKED)
        .build());
    // When & then
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION)).isEmpty();
  }

}
