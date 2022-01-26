package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.LineRangeSmallerThenSublineRangeException;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.model.SearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.LineValidation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

class LineServiceTest {

  private static final long ID = 1L;

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private LineRepository lineRepository;

  @Mock
  private VersionableService versionableService;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private LineValidation lineValidation;

  @Mock
  private SpecificationBuilderProvider specificationBuilderProvider;

  @Mock
  private SpecificationBuilderService<Line> specificationBuilderService;

  @Mock
  private Specification<Line> lineSpecification;

  private LineService lineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineService = new LineService(lineVersionRepository, lineRepository, versionableService,
        sublineVersionRepository, lineValidation, specificationBuilderProvider);
  }

  @Test
  void shouldGetPagableLinesFromRepository() {
    // Given
    when(lineSpecification.and(any())).thenReturn(lineSpecification);
    when(specificationBuilderService.buildSearchCriteriaSpecification(any())).thenReturn(
        lineSpecification);
    when(specificationBuilderProvider.getLineSpecificationBuilderService()).thenReturn(
        specificationBuilderService);
    Pageable pageable = Pageable.unpaged();

    // When
    lineService.findAll(SearchRestrictions.<LineType>builder().pageable(pageable).build());

    // Then
    verify(lineRepository).findAll(ArgumentMatchers.<Specification<Line>>any(), eq(pageable));
    verify(specificationBuilderProvider).getLineSpecificationBuilderService();
    verify(specificationBuilderService).buildSearchCriteriaSpecification(List.of());
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
    LineVersion result = lineService.save(lineVersion);

    // Then
    verify(lineValidation).validateLineBusinessRule(lineVersion);
    verify(lineVersionRepository).save(lineVersion);
    assertThat(result).isEqualTo(lineVersion);
  }

  @Test
  void shouldDeleteLinesWhenNotFound() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(List.of());

    //When & Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
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
    when(lineVersionRepository.existsById(ID)).thenReturn(true);

    // When
    lineService.deleteById(ID);

    // Then
    verify(lineVersionRepository).existsById(ID);
    verify(lineVersionRepository).deleteById(ID);
  }

  @Test
  void shouldNotDeleteLineWhenNotFound() {
    // Given
    when(lineVersionRepository.existsById(ID)).thenReturn(false);

    // When
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> lineService.deleteById(ID));

    // Then
    verify(lineVersionRepository).existsById(ID);
  }

  @Test
  void shouldNotSaveWhenThrowLineRangeSmallerThenSublineRangeException() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    doThrow(LineRangeSmallerThenSublineRangeException.class).when(lineValidation)
                                                            .validateLineBusinessRule(
                                                                lineVersion);

    // When
    assertThatExceptionOfType(LineRangeSmallerThenSublineRangeException.class).isThrownBy(
        () -> lineService.save(lineVersion));

    verify(lineVersionRepository, never()).save(lineVersion);

  }

  @Test
  void shouldNotSaveWhenThrowLineConflictException() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    doThrow(LineConflictException.class).when(lineValidation)
                                                            .validateLineBusinessRule(
                                                                lineVersion);

    // When
    assertThatExceptionOfType(LineConflictException.class).isThrownBy(
        () -> lineService.save(lineVersion));

    verify(lineVersionRepository, never()).save(lineVersion);

  }

  @Test
  void shouldNotSaveWhenThrowTemporaryLineValidationException() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    doThrow(TemporaryLineValidationException.class).when(lineValidation)
                                                   .validateLineBusinessRule(
                                                                lineVersion);

    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineService.save(lineVersion));

    verify(lineVersionRepository, never()).save(lineVersion);

  }

}
