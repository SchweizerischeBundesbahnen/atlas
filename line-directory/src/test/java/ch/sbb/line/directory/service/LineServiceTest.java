package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.LineVersionBuilder;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.LineDeleteConflictException;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.model.search.LineSearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.LineUpdateValidationService;
import ch.sbb.line.directory.validation.LineValidationService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class LineServiceTest {

  private static final long ID = 1L;

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private LineRepository lineRepository;

  @Mock
  private VersionableService versionableService;

  @Mock
  private LineValidationService lineValidationService;

  @Mock
  private LineUpdateValidationService lineUpdateValidationService;

  @Mock
  private LineSearchRestrictions lineSearchRestrictions;

  @Mock
  private LineStatusDecider lineStatusDecider;

  private LineService lineService;

  @Mock
  private SublineShorteningService sublineShorteningService;

  @Mock
  private SublineService sublineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineService = new LineService(lineVersionRepository, sublineVersionRepository, lineRepository,
        versionableService, lineValidationService, lineUpdateValidationService, lineStatusDecider,
        sublineShorteningService, sublineService);
  }

  @Test
  void shouldGetPagableLinesFromRepository() {
    // Given
    Pageable pageable = Pageable.unpaged();
    when(lineSearchRestrictions.getSpecification()).thenReturn(SpecificationBuilder.<Line>builder()
        .build()
        .searchCriteriaSpecification(
            List.of(
                "test")));
    when(lineSearchRestrictions.getPageable()).thenReturn(pageable);

    // When
    lineService.findAll(lineSearchRestrictions);

    // Then
    verify(lineRepository).findAll(ArgumentMatchers.<Specification<Line>>any(), eq(pageable));
  }

  @Test
  void shouldGetLineVersions() {
    // Given
    String slnid = "slnid";

    // When
    lineService.findLineVersions(slnid);

    // Then
    verify(lineVersionRepository).findAllBySlnidOrderByValidFrom(slnid);
  }

  @Test
  void shouldFindLineVersionsForV1() {
    // Given
    String slnid = "slnid";
    LineVersion lineVersion = LineTestData.lineVersion();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(List.of(lineVersion));

    // When SwissLineNumber Given Then it should return
    List<LineVersion> result = lineService.findLineVersionsForV1(slnid);
    assertThat(result).hasSize(1);

    // When SwissLineNumber Not given Then it should return empty
    lineVersion.setSwissLineNumber(null);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(List.of(lineVersion));

    result = lineService.findLineVersionsForV1(slnid);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldGetLine() {
    // Given
    String slnid = "slnid";

    // When
    lineService.findLine(slnid);

    // Then
    verify(lineRepository).findAllBySlnid(slnid);
  }

  @Test
  void shouldGetLineFromRepository() {
    // Given
    when(lineVersionRepository.findById(anyLong())).thenReturn(Optional.empty());
    // When
    Optional<LineVersion> result = lineService.findById(ID);

    // Then
    verify(lineVersionRepository).findById(ID);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldSaveLineWithValidation() {
    // Given
    when(lineVersionRepository.save(any())).thenAnswer(i -> i.getArgument(0, LineVersion.class));
    when(lineVersionRepository.findSwissLineNumberOverlaps(any())).thenReturn(
        Collections.emptyList());
    LineVersion lineVersion = LineTestData.lineVersion();
    // When
    LineVersion result = lineService.save(lineVersion, Optional.empty(), Collections.emptyList());

    // Then
    verify(lineValidationService).validateLinePreconditionBusinessRule(lineVersion);
    verify(lineVersionRepository).saveAndFlush(lineVersion);
    assertThat(result).isEqualTo(lineVersion);
  }

  @Test
  void shouldCreateLineWithAfterVersioningValidation() {
    // Given
    when(lineVersionRepository.save(any())).thenAnswer(i -> i.getArgument(0, LineVersion.class));
    LineVersion lineVersion = LineTestData.lineVersion();
    // When
    LineVersion result = lineService.createV2(lineVersion);

    // Then
    verify(lineValidationService).dynamicBeanValidation(lineVersion);
    verify(lineVersionRepository).saveAndFlush(lineVersion);
    verify(lineValidationService).validateLineAfterVersioningBusinessRule(lineVersion);
    assertThat(result).isEqualTo(lineVersion);
  }

  @Test
  void shouldUpdateLineWithAfterVersioningValidation() {
    // Given
    when(lineVersionRepository.save(any())).thenAnswer(i -> i.getArgument(0, LineVersion.class));
    LineVersion currentVersion = LineTestData.lineVersionBuilder().version(0).build();
    LineVersion editedVersion = LineTestData.lineVersionBuilder().version(0).build();
    // When
    List<LineVersion> currentVersions = List.of(currentVersion);
    lineService.update(currentVersion, editedVersion, currentVersions);

    // Then
    verify(lineValidationService).validateNotRevoked(currentVersion);
    verify(lineUpdateValidationService).validateFieldsNotUpdatableForLineTypeOrderly(currentVersion, editedVersion);
    verify(sublineShorteningService).isOnlyValidityChanged(currentVersion, editedVersion);
    verify(sublineShorteningService).isShortening(currentVersion, editedVersion);
    verify(lineUpdateValidationService).validateLineForUpdate(any(), any(), any());
    verify(lineUpdateValidationService).validateVersioningNotAffectingReview(any(), any());
    verify(lineValidationService).validateLineAfterVersioningBusinessRule(editedVersion);
  }

  @Test
  void shouldDeleteLinesWhenNotFound() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(List.of());

    //When & Then
    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> lineService.deleteAll(slnid));
  }

  @Test
  void shouldThrowLineDeleteConflictException() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    LineVersion lineVersion = LineVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .description("desc")
        .build();
    List<LineVersion> lineVersions = List.of(lineVersion);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(lineVersions);
    SublineVersion sublineVersion = SublineVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .description("desc")
        .build();
    List<SublineVersion> sublineVersions = List.of(sublineVersion);
    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(slnid)).thenReturn(
        sublineVersions);

    //When & Then
    assertThatExceptionOfType(LineDeleteConflictException.class).isThrownBy(
        () -> lineService.deleteAll(slnid));
  }

  @Test
  void shouldDeleteLines() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    LineVersion lineVersion = LineVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .description("desc")
        .build();
    List<LineVersion> lineVersions = List.of(lineVersion);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(lineVersions);

    //When
    lineService.deleteAll(slnid);
    //Then
    verify(lineVersionRepository).deleteAll(lineVersions);
  }

  @Test
  void shouldDeleteLine() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    lineVersion.setId(1L);
    when(lineVersionRepository.existsById(1L)).thenReturn(true);

    // When
    lineService.deleteById(1L);

    // Then
    verify(lineVersionRepository).deleteById(1L);
  }

  @Test
  void shouldNotDeleteLineWhenNotFound() {
    // Given
    when(lineVersionRepository.findById(ID)).thenReturn(Optional.empty());

    // When
    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> lineService.deleteById(ID));
  }

  @Test
  void shouldNotSaveWhenThrowLineConflictException() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    doThrow(LineConflictException.class).when(lineValidationService)
        .validateLinePreconditionBusinessRule(
            lineVersion);

    // When
    assertThatExceptionOfType(LineConflictException.class).isThrownBy(
        () -> lineService.save(lineVersion, Optional.empty(), Collections.emptyList()));

    verify(lineVersionRepository, never()).save(lineVersion);

  }

  @Test
  void shouldNotSaveWhenThrowTemporaryLineValidationException() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    doThrow(TemporaryLineValidationException.class).when(lineValidationService)
        .validateLinePreconditionBusinessRule(
            lineVersion);

    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineService.save(lineVersion, Optional.empty(), Collections.emptyList()));

    verify(lineVersionRepository, never()).save(lineVersion);

  }

  @Test
  void shouldThrowStaleExceptionOnDifferentVersion() {
    //given
    LineVersionBuilder<?, ?> version = LineVersion.builder().slnid("slnid");

    Executable executable = () -> lineService.updateVersion(version.version(1).build(),
        version.version(0).build());
    assertThrows(StaleObjectStateException.class, executable);
    //then
    verify(lineVersionRepository).incrementVersion("slnid");
  }

}
