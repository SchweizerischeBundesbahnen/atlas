package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.model.search.SublineSearchRestrictions;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class SublineServiceSearchTest {

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final SublineService sublineService;
  private SublineVersion version1;
  private SublineVersion version2;
  private SublineVersion version3;

  @Autowired
   SublineServiceSearchTest(
      SublineVersionRepository sublineVersionRepository,
      LineVersionRepository lineVersionRepository,
      SublineService sublineService) {
    this.sublineVersionRepository = sublineVersionRepository;
    this.lineVersionRepository = lineVersionRepository;
    this.sublineService = sublineService;
  }

  @BeforeEach
  void init() {
    lineVersionRepository.save(
        LineTestData.lineVersionBuilder().slnid(SublineTestData.MAINLINE_SLNID).build());
    version1 = SublineTestData.sublineVersionBuilder().slnid("ch:slnid:1")
        .swissSublineNumber("1")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    version2 = SublineTestData.sublineVersionBuilder().slnid("ch:slnid:2")
        .swissSublineNumber("2")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();
    version3 = SublineTestData.sublineVersionBuilder().slnid("ch:slnid:3")
        .swissSublineNumber("3")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
  }

  @AfterEach
  void cleanUp() {
    sublineVersionRepository.deleteAll();
  }

  @Test
  void shouldFindVersionWithValidOn() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .validOn(Optional.of(LocalDate.of(2020, 1, 1)))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithValidOn() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder().pageable(Pageable.unpaged()).validOn(
            Optional.of(LocalDate.of(2019, 1, 1))).build());

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindAllVersionOnNoRestrictions() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder().pageable(Pageable.unpaged()).build());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithNoGivenValidOn() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder().pageable(Pageable.unpaged()).build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindOrderedVersionWithNoGivenValidOn() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 20,
                Sort.by("swissSublineNumber").ascending()))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getSwissSublineNumber()).isEqualTo("1");
  }

  @Test
  void shouldFindDescOrderedVersionWithNoGivenValidOn() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(PageRequest.of(0, 20,
                Sort.by("swissSublineNumber").descending()))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getSwissSublineNumber()).isEqualTo("2");
  }

  @Test
  void shouldFindVersionWithText() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("1"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleTexts() {
    // Given
    version1.setDescription("Luca ist der grösste YB-Fan");
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("1", "ch:SLNID:1", "yb", "Fan"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithText() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("2"))
            .build());

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindVersionWithStatus() {
    // Given
    version1.setDescription("Luca ist der grösste YB-Fan");
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("1", "ch:SLNID:1", "yb", "Fan"))
            .statusRestrictions(List.of(Status.VALIDATED))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleStatus() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);

    version2.setStatus(Status.DRAFT);
    sublineVersionRepository.saveAndFlush(version2);

    version3.setStatus(Status.REVOKED);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .statusRestrictions(
                List.of(Status.VALIDATED, Status.DRAFT))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldFindVersionWithType() {
    // Given
    version1.setDescription("Luca ist der grösste YB-Fan");
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(
                List.of("1", "ch:SLNID:1", "yb", "Fan"))
            .typeRestrictions(List.of(SublineType.TECHNICAL))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleTypes() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);

    version3.setSublineType(SublineType.COMPENSATION);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .typeRestrictions(List.of(
                SublineType.TECHNICAL,
                SublineType.COMPENSATION))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithUnderscore() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    SublineVersion versionWithUnderscore = SublineTestData.sublineVersionBuilder()
        .slnid("ch:slnid:4")
        .swissSublineNumber("1_")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    sublineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("_"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleUnderscore() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    SublineVersion versionWithUnderscore = SublineTestData.sublineVersionBuilder()
        .slnid("ch:slnid:4")
        .swissSublineNumber("1__")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    sublineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("__"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithPercente() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    SublineVersion versionWithUnderscore = SublineTestData.sublineVersionBuilder()
        .slnid("ch:slnid:4")
        .swissSublineNumber("1%")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    sublineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("%"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultiplePercente() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);
    sublineVersionRepository.saveAndFlush(version3);
    SublineVersion versionWithUnderscore = SublineTestData.sublineVersionBuilder()
        .slnid("ch:slnid:4")
        .swissSublineNumber("1%%")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    sublineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Subline> result = sublineService.findAll(
        SublineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(List.of("%%"))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

}
