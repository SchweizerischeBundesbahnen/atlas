package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class LineVersionRepositoryTest {

  private static final LineVersion LINE_VERSION = LineTestData.lineVersion();

  private final LineVersionRepository lineVersionRepository;

  @Autowired
   LineVersionRepositoryTest(LineVersionRepository lineVersionRepository) {
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

    assertThat(result.getCreator()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
    assertThat(result.getEditor()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION).isEmpty()).isTrue();

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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION).isEmpty()).isFalse();

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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION).isEmpty()).isFalse();

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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(LINE_VERSION).isEmpty()).isFalse();

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
    assertThat(lineVersionRepository.findSwissLineNumberOverlaps(entity).isEmpty()).isTrue();

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

  @Test
  void shouldGetFullLineVersions() {
    //given
    lineVersionRepository.save(LINE_VERSION);
    LineVersion lineVersion2 = LineTestData.lineVersionBuilder()
                                           .description("desc2")
                                           .lineType(LineType.OPERATIONAL)
                                           .build();
    LineVersion lineVersion3 = LineTestData.lineVersionBuilder()
                                           .description("desc3")
                                           .lineType(LineType.ORDERLY)
                                           .build();
    lineVersionRepository.save(lineVersion2);
    lineVersionRepository.save(lineVersion3);
    //when
    List<LineVersion> result = lineVersionRepository.getFullLineVersions();

    //then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldGetActualLineVersions() {
    //given
    LineVersion lineVersion1 = LineTestData.lineVersionBuilder()
                                           .validFrom(LocalDate.of(2022, 1, 1))
                                           .validTo(LocalDate.of(2022, 1, 31))
                                           .description("desc1")
                                           .lineType(LineType.OPERATIONAL)
                                           .build();
    LineVersion lineVersion2 = LineTestData.lineVersionBuilder()
                                           .validFrom(LocalDate.of(2022, 2, 1))
                                           .validTo(LocalDate.of(2022, 12, 31))
                                           .description("desc2")
                                           .lineType(LineType.ORDERLY)
                                           .build();
    LineVersion lineVersion3 = LineTestData.lineVersionBuilder()
                                           .validFrom(LocalDate.of(2021, 1, 1))
                                           .validTo(LocalDate.of(2021, 12, 31))
                                           .description("desc3")
                                           .lineType(LineType.ORDERLY)
                                           .build();
    lineVersionRepository.save(lineVersion1);
    lineVersionRepository.save(lineVersion2);
    lineVersionRepository.save(lineVersion3);
    //when
    List<LineVersion> result = lineVersionRepository.getActualLineVersions(
        LocalDate.of(2022, 1, 1));

    //then
    assertThat(result).hasSize(1).contains(lineVersion1);

  }

}
