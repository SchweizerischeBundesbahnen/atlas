package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.Collections;
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
public class SublineServiceSearchTest {

  private static final String SLNID = "ch:1:slnid:100000";
  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final SublineService sublineService;
  private SublineVersion version1;
  private SublineVersion version2;
  private SublineVersion version3;

  @Autowired
  public SublineServiceSearchTest(
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
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(), Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(),
        Optional.of(LocalDate.of(2020, 1, 1)));

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
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(), Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(),
        Optional.of(LocalDate.of(2019, 1, 1)));

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
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(), Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }

  @Test
  void shouldFindVersionWithNoGivenValidOn() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    // When
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(), Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

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
        PageRequest.of(0, 20, Sort.by("swissSublineNumber").ascending()), Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

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
        PageRequest.of(0, 20, Sort.by("swissSublineNumber").descending()), Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getSwissSublineNumber()).isEqualTo("2");
  }

  @Test
  void shouldFindVersionWithText() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    // When
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(), List.of("1"),
        Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

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
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(),
        List.of("1", "ch:SLNID:1", "yb", "Fan"), Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionWithText() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    // When
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(),
        List.of("2"), Collections.emptyList(), Collections.emptyList(),
        Optional.empty());

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
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(),
        List.of("1", "ch:SLNID:1", "yb", "Fan"), List.of(Status.ACTIVE), Collections.emptyList(),
        Optional.empty());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleStatus() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);

    version2.setStatus(Status.NEEDS_REVIEW);
    sublineVersionRepository.saveAndFlush(version2);

    version3.setStatus(Status.REVIEWED);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(),
        Collections.emptyList(), List.of(Status.ACTIVE, Status.NEEDS_REVIEW),
        Collections.emptyList(),
        Optional.empty());

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
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(),
        List.of("1", "ch:SLNID:1", "yb", "Fan"), Collections.emptyList(),
        List.of(SublineType.TECHNICAL),
        Optional.empty());

    // Then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindVersionWithMultipleTypes() {
    // Given
    sublineVersionRepository.saveAndFlush(version1);
    sublineVersionRepository.saveAndFlush(version2);

    version3.setType(SublineType.COMPENSATION);
    sublineVersionRepository.saveAndFlush(version3);
    // When
    Page<Subline> result = sublineService.findAll(Pageable.unpaged(),
        Collections.emptyList(), Collections.emptyList(),
        List.of(SublineType.TECHNICAL, SublineType.COMPENSATION),
        Optional.empty());

    // Then
    assertThat(result.getContent()).hasSize(3);
  }
}