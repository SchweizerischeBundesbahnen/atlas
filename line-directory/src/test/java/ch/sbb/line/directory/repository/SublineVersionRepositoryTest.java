package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class SublineVersionRepositoryTest {

  private final SublineVersionRepository sublineVersionRepository;
  private SublineVersion defaultSublineVersion;

  @Autowired
   SublineVersionRepositoryTest(SublineVersionRepository sublineVersionRepository) {
    this.sublineVersionRepository = sublineVersionRepository;
  }

  @BeforeEach
  void setUp() {
    defaultSublineVersion = SublineTestData.sublineVersion();
  }

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    sublineVersionRepository.save(defaultSublineVersion);

    //when
    SublineVersion result = sublineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(
        defaultSublineVersion);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");
    assertThat(result.getSlnid()).startsWith(result.getMainlineSlnid());

    assertThat(result.getCreationDate()).isNotNull();
    assertThat(result.getEditionDate()).isNotNull();

    assertThat(result.getCreator()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
    assertThat(result.getEditor()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
  }

  @Test
  void shouldGetCountVersions() {
    //when
    sublineVersionRepository.save(defaultSublineVersion);
    long result = sublineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    SublineVersion sublineVersion = sublineVersionRepository.save(defaultSublineVersion);

    //when
    sublineVersionRepository.delete(sublineVersion);
    List<SublineVersion> result = sublineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteVersions() {
    //given
    SublineVersion sublineVersion = sublineVersionRepository.saveAndFlush(defaultSublineVersion);
    String slnid = sublineVersion.getSlnid();
    List<SublineVersion> sublineVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        slnid);
    assertThat(sublineVersions).hasSize(1);

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
    sublineVersionRepository.save(
        SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2019, 1, 1))
                       .validTo(LocalDate.of(2019, 12, 31))
                       .build());
    sublineVersionRepository.save(
        SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2021, 1, 1))
                       .validTo(LocalDate.of(2021, 12, 31))
                       .build());
    // When
    assertThat(
        sublineVersionRepository.findSwissLineNumberOverlaps(defaultSublineVersion)).isEmpty();

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
    assertThat(
        sublineVersionRepository.findSwissLineNumberOverlaps(defaultSublineVersion)).isNotEmpty();

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
    assertThat(
        sublineVersionRepository.findSwissLineNumberOverlaps(defaultSublineVersion)).isNotEmpty();

    // Then
  }

  /**
   * New:                   |____1____|
   * Current: |--------1--------|
   */
  @Test
  void shouldNotAllowSwissNumberOnOverlapEnd() {
    // Given
    sublineVersionRepository.save(
        SublineTestData.sublineVersionBuilder().validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2020, 10, 31))
            .build());

    // Then
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(defaultSublineVersion)).isNotEmpty();
  }

  /**
   * New:     |____1____|
   * Current: |----1----|
   */
  @Test
  void shouldAllowUpdateOnSameLineVersion() {
    // Given
    sublineVersionRepository.save(defaultSublineVersion);
    // Then
    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(defaultSublineVersion)).isEmpty();
  }

  @Test
  void shouldAllowRevokedSwissNumberOnOverlapBetween() {
    sublineVersionRepository.save(SublineTestData.sublineVersionBuilder()
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .swissSublineNumber("SWISSSublineNUMBER")
        .status(Status.REVOKED)
        .build());

    assertThat(sublineVersionRepository.findSwissLineNumberOverlaps(defaultSublineVersion)).isEmpty();
  }

  @Test
  void shouldGetFullLineVersions() {
    //given
    sublineVersionRepository.save(defaultSublineVersion);

    //when
    List<SublineVersion> result = sublineVersionRepository.getFullSublineVersions();

    //then
    assertThat(result).hasSize(1).containsAll(result);
  }

  @Test
  void shouldGetActualLineVersions() {
    //given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    sublineVersion.setValidTo(LocalDate.of(2099, 1, 1));
    sublineVersionRepository.save(sublineVersion);

    //when
    List<SublineVersion> result = sublineVersionRepository.getActualSublineVersions(
        LocalDate.of(2022, 1, 1));

    //then
    assertThat(result).hasSize(1).containsAll(result);
  }
}
