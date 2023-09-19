package ch.sbb.line.directory.service;

import static ch.sbb.line.directory.converter.CmykColorConverter.fromCmykString;
import static ch.sbb.line.directory.converter.RgbColorConverter.fromHex;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.entity.LineVersionSnapshot.LineVersionSnapshotBuilder;
import ch.sbb.line.directory.model.search.LineVersionSnapshotSearchRestrictions;
import ch.sbb.line.directory.repository.LineVersionSnapshotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 class LineVersionSnapshotSearchTest {

  private final LineVersionSnapshotRepository lineVersionSnapshotRepository;
  private final LineVersionSnapshotService lineVersionSnapshotService;
  private LineVersionSnapshot version1;
  private LineVersionSnapshot version2;
  private LineVersionSnapshot version3;

  @Autowired
   LineVersionSnapshotSearchTest(LineVersionSnapshotRepository lineVersionSnapshotRepository,
      LineVersionSnapshotService lineVersionSnapshotService) {
    this.lineVersionSnapshotRepository = lineVersionSnapshotRepository;
    this.lineVersionSnapshotService = lineVersionSnapshotService;
  }

  @BeforeEach
  void init() {

    version1 = getBaseVersionBuilder().slnid("ch:slnid:1")
        .swissLineNumber("1")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    version2 = getBaseVersionBuilder().slnid("ch:slnid:2")
        .swissLineNumber("2")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();
    version3 = getBaseVersionBuilder().slnid("ch:slnid:3")
        .swissLineNumber("3")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
  }

  @AfterEach
  void cleanUp() {
    lineVersionSnapshotRepository.deleteAll();
  }

  @Test
  void shouldFindVersionWithValidOn() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .validOn(Optional.of(LocalDate.of(2020, 1, 1)))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithValidOn() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder().pageable(Pageable.unpaged()).validOn(
            Optional.of(LocalDate.of(2019, 1, 1))).build());

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindAllVersionOnNoRestrictions() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder().pageable(Pageable.unpaged()).build());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithNoGivenValidOn() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder().pageable(Pageable.unpaged()).build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindOrderedVersionWithNoGivenValidOn() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
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
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
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
    version1.setNumber("1");
    lineVersionSnapshotRepository.saveAndFlush(version1);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("1"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithUnderscore() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    LineVersionSnapshot versionWithUnderscore = getBaseVersionBuilder().slnid("ch:slnid:4")
        .number("1_")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionSnapshotRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("_"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleUnderscore() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    LineVersionSnapshot versionWithUnderscore = getBaseVersionBuilder().slnid("ch:slnid:4")
        .number("1__")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionSnapshotRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("__"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithPercent() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    LineVersionSnapshot versionWithUnderscore = getBaseVersionBuilder().slnid("ch:slnid:4")
        .number("1%")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionSnapshotRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("%"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultiplePercent() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    LineVersionSnapshot versionWithUnderscore = getBaseVersionBuilder().slnid("ch:slnid:4")
        .number("1%%")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    lineVersionSnapshotRepository.saveAndFlush(versionWithUnderscore);

    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
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
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(
            List.of("Luca", "Fan",
                "yb", "grösste"))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithText() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .searchCriterias(List.of("2123"))
        .build());

    // Then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindVersionWithStatus() {
    // Given
    version1.setDescription("Luca ist der grösste YB-Fan");
    version1.setWorkflowStatus(WorkflowStatus.APPROVED);
    lineVersionSnapshotRepository.saveAndFlush(version1);
    lineVersionSnapshotRepository.saveAndFlush(version2);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .statusRestrictions(List.of(WorkflowStatus.APPROVED))
            .build());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleStatus() {
    // Given
    lineVersionSnapshotRepository.saveAndFlush(version1);

    version2.setWorkflowStatus(WorkflowStatus.APPROVED);
    lineVersionSnapshotRepository.saveAndFlush(version2);

    version3.setWorkflowStatus(WorkflowStatus.ADDED);
    lineVersionSnapshotRepository.saveAndFlush(version3);
    // When
    Page<LineVersionSnapshot> result = lineVersionSnapshotService.findAll(LineVersionSnapshotSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .statusRestrictions(
            List.of(WorkflowStatus.STARTED, WorkflowStatus.APPROVED))
        .build());

    // Then
    assertThat(result.getContent()).hasSize(2);
  }

  private LineVersionSnapshotBuilder getBaseVersionBuilder() {
    return LineVersionSnapshot.builder()
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .workflowStatus(WorkflowStatus.STARTED)
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .alternativeName("alternativeName")
        .combinationName("combinationName")
        .longName("longName")
        .colorFontRgb(fromHex("#FFFFFF"))
        .colorBackRgb(fromHex("#FFFFFF"))
        .colorFontCmyk(fromCmykString("0,0,0,0"))
        .colorBackCmyk(fromCmykString("0,0,0,0"))
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.now())
        .editionDate(LocalDateTime.now())
        .editor("Marek")
        .creator("Hamsik")
        .businessOrganisation("businessOrganisation")
        .comment("comment")
        .workflowId(123L)
        .version(1)
        .parentObjectId(123L)
        .description("b0.IC2")
        .swissLineNumber("swissLineNumber")
        .slnid("b0.IC2");
  }
}
