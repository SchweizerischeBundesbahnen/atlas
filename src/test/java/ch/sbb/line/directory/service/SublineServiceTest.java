package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.SublineRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

class SublineServiceTest {

  private static final long ID = 1L;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private SublineRepository sublineRepository;

  private SublineService sublineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineService = new SublineService(sublineVersionRepository, sublineRepository);
  }

  @Test
  void shouldGetPagableLinesFromRepository() {
    // Given
    Pageable pageable = Pageable.unpaged();

    // When
    sublineService.findAll(pageable);

    // Then
    verify(sublineRepository).findAll(pageable);
  }

  @Test
  void shouldGetLineFromRepository() {
    // Given
    when(sublineVersionRepository.findById(anyLong())).thenReturn(Optional.empty());
    // When
    Optional<SublineVersion> result = sublineService.findById(ID);

    // Then
    verify(sublineVersionRepository).findById(ID);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldSaveSublineWithValidation() {
    // Given
    when(sublineVersionRepository.save(any())).thenAnswer(i -> i.getArgument(0, SublineVersion.class));
    when(sublineVersionRepository.hasUniqueSwissSublineNumber(any())).thenReturn(true);
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    // When
    SublineVersion result = sublineService.save(sublineVersion);

    // Then
    verify(sublineVersionRepository).hasUniqueSwissSublineNumber(sublineVersion);
    verify(sublineVersionRepository).save(sublineVersion);
    assertThat(result).isEqualTo(sublineVersion);
  }

  @Test
  void shouldDeleteSubline() {
    // Given
    when(sublineVersionRepository.existsById(ID)).thenReturn(true);

    // When
    sublineService.deleteById(ID);

    // Then
    verify(sublineVersionRepository).existsById(ID);
    verify(sublineVersionRepository).deleteById(ID);
  }

  @Test
  void shouldNotDeleteSublineWhenNotFound() {
    // Given
    when(sublineVersionRepository.existsById(ID)).thenReturn(false);

    // When
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(()-> sublineService.deleteById(ID));

    // Then
    verify(sublineVersionRepository).existsById(ID);
  }
}