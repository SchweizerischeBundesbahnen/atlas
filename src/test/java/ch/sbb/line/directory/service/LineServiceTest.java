package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
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

  private LineService lineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineService = new LineService(lineVersionRepository);
  }

  @Test
  void shouldGetPagableLinesFromRepository() {
    // Given
    Pageable pageable = Pageable.unpaged();

    // When
    lineService.findAll(pageable);

    // Then
    verify(lineVersionRepository).findAll(pageable);
  }

  @Test
  void shouldGetTotalLinesFromRepository() {
    // Given
    Long totalCount = ID;
    when(lineVersionRepository.count()).thenReturn(totalCount);
    // When
    long result = lineService.totalCount();

    // Then
    verify(lineVersionRepository).count();
    assertThat(result).isEqualTo(totalCount);
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