package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.WithMockJwtAuthentication;
import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class SublineVersionRepositoryTest {

  private static final SublineVersion SUBLINE_VERSION = SublineTestData.sublineVersion();
  private final SublineVersionRepository sublineVersionRepository;

  @Autowired
  public SublineVersionRepositoryTest(SublineVersionRepository sublineVersionRepository) {
    this.sublineVersionRepository = sublineVersionRepository;
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    sublineVersionRepository.save(SUBLINE_VERSION);

    //when
    SublineVersion result = sublineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(
        SUBLINE_VERSION);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");

    assertThat(result.getCreationDate()).isNotNull();
    assertThat(result.getEditionDate()).isNotNull();

    assertThat(result.getCreator()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
    assertThat(result.getEditor()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
  }

  @Test
  void shouldGetCountVersions() {
    //when
    sublineVersionRepository.save(SUBLINE_VERSION);
    long result = sublineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    SublineVersion sublineVersion = sublineVersionRepository.save(SUBLINE_VERSION);

    //when
    sublineVersionRepository.delete(sublineVersion);
    List<SublineVersion> result = sublineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteVersions() {
    //given
    SublineVersion sublineVersion = sublineVersionRepository.saveAndFlush(SUBLINE_VERSION);
    String slnid = sublineVersion.getSlnid();
    List<SublineVersion> sublineVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    assertThat(sublineVersions.size()).isEqualTo(1);

    //when
    sublineVersionRepository.deleteAll(sublineVersions);
    List<SublineVersion> result = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        slnid);

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
    sublineVersionRepository.save(SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2019, 1, 1))
                                           .validTo(LocalDate.of(2019, 12, 31))
                                           .build());
    sublineVersionRepository.save(SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2021, 1, 1))
                                           .validTo(LocalDate.of(2021, 12, 31))
                                           .build());
    // When
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(SUBLINE_VERSION).isEmpty()).isTrue();

    // Then
  }

  /**
   * New:           |____1____|
   * Current:   |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBetween() {
    // Given
    sublineVersionRepository.save(SublineTestData.sublineVersionBuilder()
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .swissSublineNumber("SWISSSublineNUMBER")
        .build());
    // When
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(SUBLINE_VERSION).isEmpty()).isFalse();

    // Then
  }

  /**
   * New:         |____1____|
   * Current:         |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapBeginning() {
    // Given
    sublineVersionRepository.save(
        SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2020, 10, 1))
                    .validTo(LocalDate.of(2099, 12, 31))
                    .build());
    // When
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(SUBLINE_VERSION).isEmpty()).isFalse();

    // Then
  }

  /**
   * New:                   |____1____|
   * Current: |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapEnd() {
    // Given
    sublineVersionRepository.save(SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2000, 1, 1))
                                           .validTo(LocalDate.of(2020, 10, 31))
                                           .build());
    // When
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(SUBLINE_VERSION).isEmpty()).isFalse();

    // Then
  }

  /**
   * New:     |____1____|
   * Current: |----1----|
   */
  @Test
  void shouldAllowUpdateOnSameLineVersion() {
    // Given
    SublineVersion entity = sublineVersionRepository.save(SUBLINE_VERSION);
    // When
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(SUBLINE_VERSION).isEmpty()).isTrue();

    // Then
  }
}
