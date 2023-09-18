package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.model.search.LineSearchRestrictions;
import ch.sbb.line.directory.repository.LineVersionRepository;
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
 class LineServiceSearchTest {

  private final LineVersionRepository lineVersionRepository;
  private final LineService lineService;
  private LineVersion version1;
  private LineVersion version2;
  private LineVersion version3;

  @Autowired
   LineServiceSearchTest(LineVersionRepository lineVersionRepository,
      LineService lineService) {
    this.lineVersionRepository = lineVersionRepository;
    this.lineService = lineService;
  }

  @BeforeEach
  void init() {
    version1 = LineTestData.lineVersionBuilder().slnid("ch:slnid:1")
        .swissLineNumber("1")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    version2 = LineTestData.lineVersionBuilder().slnid("ch:slnid:2")
        .swissLineNumber("2")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();
    version3 = LineTestData.lineVersionBuilder().slnid("ch:slnid:3")
        .swissLineNumber("3")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
  }

  @AfterEach
  void cleanUp() {
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldFindVersionWithValidOn() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .validOn(Optional.of(LocalDate.of(2020, 1, 1)))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithValidOn() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder().pageable(Pageable.unpaged()).validOn(
            Optional.of(LocalDate.of(2019, 1, 1))).build());

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindAllVersionOnNoRestrictions() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder().pageable(Pageable.unpaged()).build());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithNoGivenValidOn() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    // When
    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder().pageable(Pageable.unpaged()).build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindOrderedVersionWithNoGivenValidOn() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(PageRequest.of(0, 20,
            Sort.by("swissLineNumber")
                .ascending()))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getSwissLineNumber()).isEqualTo("1");
  }

  @Test
  void shouldFindDescOrderedVersionWithNoGivenValidOn() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(PageRequest.of(0, 20,
            Sort.by("swissLineNumber")
                .descending()))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getSwissLineNumber()).isEqualTo("2");
  }

  @Test
  void shouldFindVersionWithText() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("1"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithUnderscore() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    LineVersion versionWithUnderscore = LineTestData.lineVersionBuilder().slnid("ch:slnid:4")
        .swissLineNumber("1_")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("_"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleUnderscore() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    LineVersion versionWithUnderscore = LineTestData.lineVersionBuilder().slnid("ch:slnid:4")
        .swissLineNumber("1__")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("__"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithPercent() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    LineVersion versionWithUnderscore = LineTestData.lineVersionBuilder().slnid("ch:slnid:4")
        .swissLineNumber("1%")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("%"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultiplePercent() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    LineVersion versionWithUnderscore = LineTestData.lineVersionBuilder().slnid("ch:slnid:4")
        .swissLineNumber("1%%")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("%%"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleTexts() {
    // Given
    version1.setDescription("Luca ist der grösste YB-Fan");
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(
            List.of("1", "ch:SLNID:1",
                "yb", "Fan"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithText() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
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
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .searchCriterias(
                List.of("1", "ch:SLNID:1", "yb", "Fan"))
            .statusRestrictions(List.of(Status.VALIDATED))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleStatus() {
    // Given
    lineVersionRepository.saveAndFlush(version1);

    version2.setStatus(Status.DRAFT);
    lineVersionRepository.saveAndFlush(version2);

    version3.setStatus(Status.WITHDRAWN);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .statusRestrictions(
            List.of(Status.VALIDATED,
                Status.DRAFT))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldFindVersionWithType() {
    // Given
    version1.setDescription("Luca ist der grösste YB-Fan");
    lineVersionRepository.saveAndFlush(version1);
    lineVersionRepository.saveAndFlush(version2);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(
            List.of("1", "ch:SLNID:1",
                "yb", "Fan"))
        .typeRestrictions(
            List.of(LineType.ORDERLY))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleTypes() {
    // Given
    lineVersionRepository.saveAndFlush(version1);

    version2.setLineType(LineType.TEMPORARY);
    lineVersionRepository.saveAndFlush(version2);

    version3.setLineType(LineType.OPERATIONAL);
    lineVersionRepository.saveAndFlush(version3);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .typeRestrictions(
            List.of(LineType.ORDERLY,
                LineType.TEMPORARY))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
  }
}
