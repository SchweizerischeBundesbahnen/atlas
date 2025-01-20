package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineRequestParams;
import ch.sbb.atlas.api.lidi.enumaration.LidiElementType;
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
            .lineRequestParams(LineRequestParams.builder().validOn(LocalDate.of(2020, 1, 1)).build())
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
        LineSearchRestrictions.builder().pageable(Pageable.unpaged())
            .lineRequestParams(LineRequestParams.builder().validOn(LocalDate.of(2019, 1, 1)).build())
            .build());

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
        LineSearchRestrictions.builder().pageable(Pageable.unpaged())
            .lineRequestParams(LineRequestParams.builder().build())
            .build());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithNoGivenValidOn() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    // When
    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder().pageable(Pageable.unpaged())
            .lineRequestParams(LineRequestParams.builder().build()).build());

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
        .lineRequestParams(LineRequestParams.builder().build())
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().getFirst().getSwissLineNumber()).isEqualTo("1");
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
        .lineRequestParams(LineRequestParams.builder().build())
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().getFirst().getSwissLineNumber()).isEqualTo("2");
  }

  @Test
  void shouldFindVersionWithText() {
    // Given
    lineVersionRepository.saveAndFlush(version1);
    // When
    Page<Line> result = lineService.findAll(LineSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("1")).build())
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
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("_")).build())
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
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("__")).build())
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
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("%")).build())
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
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("%%")).build())
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
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("1", "ch:SLNID:1", "yb", "Fan")).build())
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
        .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("2")).build())
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
            .lineRequestParams(LineRequestParams.builder().searchCriteria(List.of("1", "ch:SLNID:1", "yb", "Fan"))
                .statusRestrictions(List.of(Status.VALIDATED)).build())
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
        .lineRequestParams(LineRequestParams.builder().statusRestrictions(List.of(Status.VALIDATED, Status.DRAFT)).build())
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
        .lineRequestParams(LineRequestParams.builder()
            .searchCriteria(List.of("1", "ch:SLNID:1", "yb", "Fan"))
            .typeRestrictions(List.of(LidiElementType.ORDERLY))
            .build())
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
        .lineRequestParams(LineRequestParams.builder()
            .typeRestrictions(
                List.of(LidiElementType.ORDERLY,
                    LidiElementType.TEMPORARY))
            .build())
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldFindVersionWithBusinessOrganisation() {
    version1 = lineVersionRepository.saveAndFlush(version1);
    String sboid = version1.getBusinessOrganisation();

    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .lineRequestParams(LineRequestParams.builder().businessOrganisation(sboid).build())
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithBusinessOrganisation() {
    version1 = lineVersionRepository.saveAndFlush(version1);
    String sboid = "unknownSboid";

    Page<Line> result = lineService.findAll(
        LineSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .lineRequestParams(LineRequestParams.builder().businessOrganisation(sboid).build())
            .build());

    // Then
    assertThat(result.getContent()).isEmpty();
  }
}
