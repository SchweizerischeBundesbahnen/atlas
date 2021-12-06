package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

class LineServiceTest {

  private static final long ID = 1L;

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private LineRepository lineRepository;

  @Mock
  private VersionableService versionableService;

  private LineService lineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineService = new LineService(lineVersionRepository,lineRepository, versionableService);
  }

  @Test
  void shouldGetPagableLinesFromRepository() {
    // Given
    Pageable pageable = Pageable.unpaged();

    // When
    lineService.findAll(pageable, Optional.empty());

    // Then
    verify(lineRepository).findAll(pageable);
  }

  @Test
  void shouldGetLine() {
    // Given
    String slnid = "slnid";

    // When
    lineService.findLineVersions(slnid);

    // Then
    verify(lineVersionRepository).findAllBySlnid(slnid);
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
    when(lineVersionRepository.hasUniqueSwissLineNumber(any())).thenReturn(true);
    LineVersion lineVersion = LineTestData.lineVersion();
    // When
    LineVersion result = lineService.save(lineVersion);

    // Then
    verify(lineVersionRepository).hasUniqueSwissLineNumber(lineVersion);
    verify(lineVersionRepository).save(lineVersion);
    assertThat(result).isEqualTo(lineVersion);
  }

  @Test
  void shouldDeleteLinesWhenNotFound() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    when(lineVersionRepository.findAllBySlnid(slnid)).thenReturn(List.of());

    //When & Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> lineService.deleteAll(slnid));;
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
    when(lineVersionRepository.findAllBySlnid(slnid)).thenReturn(lineVersions);

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
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(()->lineService.deleteById(ID));

    // Then
    verify(lineVersionRepository).existsById(ID);
  }
}