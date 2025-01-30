package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.SublineVersionBuilder;
import ch.sbb.line.directory.exception.DateRangeConflictException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.SublineValidationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SublineServiceTest {

  private static final long ID = 1L;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private LineService lineService;

  @Mock
  private VersionableService versionableService;

  @Mock
  private SublineValidationService sublineValidationService;

  @Mock
  private CoverageService coverageService;

  @Mock
  private LineVersionRepository lineVersionRepository;

  private SublineService sublineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineService = new SublineService(sublineVersionRepository, lineVersionRepository, versionableService,
        sublineValidationService,
        coverageService);
    when(sublineVersionRepository.saveAndFlush(any())).then(i -> i.getArgument(0));
  }

  @Test
  void shouldGetSubline() {
    // Given
    String slnid = "slnid";

    // When
    sublineService.findSubline(slnid);

    // Then
    verify(sublineVersionRepository).findAllBySlnidOrderByValidFrom(slnid);
  }

  @Test
  void shouldGetSublineVersionFromRepository() {
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
    when(sublineVersionRepository.save(any())).thenAnswer(
        i -> i.getArgument(0, SublineVersion.class));
    when(sublineVersionRepository.findSwissLineNumberOverlaps(any())).thenReturn(
        Collections.emptyList());
    when(lineService.findLineVersions(any())).thenReturn(
        Collections.singletonList(LineTestData.lineVersion()));
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    // When
    SublineVersion result = sublineService.save(sublineVersion);

    // Then
    verify(sublineValidationService).validatePreconditionSublineBusinessRules(sublineVersion);
    verify(sublineVersionRepository).saveAndFlush(sublineVersion);
    verify(sublineValidationService).validateSublineAfterVersioningBusinessRule(sublineVersion);
    assertThat(result).isEqualTo(sublineVersion);
  }

  @Test
  void shouldDeleteSubline() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setId(1L);
    when(sublineVersionRepository.findById(1L)).thenReturn(Optional.of(sublineVersion));

    // When
    sublineService.deleteById(1L);

    // Then
    verify(sublineVersionRepository).deleteById(1L);
  }

  @Test
  void shouldDeleteSublinesWhenNotFound() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(List.of());

    //When & Then
    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> sublineService.deleteAll(slnid));
  }

  @Test
  void shouldDeleteSublines() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder().build();
    List<SublineVersion> sublineVersions = List.of(sublineVersion);
    when(sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(
        sublineVersions);

    //When
    sublineService.deleteAll(slnid);
    //Then
    verify(sublineVersionRepository).deleteAll(sublineVersions);
  }

  @Test
  void shouldNotDeleteSublineWhenNotFound() {
    // Given
    when(sublineVersionRepository.findById(ID)).thenReturn(Optional.empty());

    // When
    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> sublineService.deleteById(ID));

  }

  @Test
  void shouldThrowStaleExceptionOnDifferentVersion() {
    //given
    SublineVersionBuilder<?, ?> version = SublineVersion.builder().slnid("slnid");

    Executable executable = () -> sublineService.updateVersion(version.version(1).build(),
        version.version(0).build());
    //then
    assertThrows(StaleObjectStateException.class, executable);
    verify(sublineVersionRepository).incrementVersion("slnid");
  }

  @Test
  void shouldReturnMainLineVersionWhenOneLineVersion() {
    //given
    String mainSlnid = "ch:1:slnid:8000";
    LineVersion lineVersion =
        LineVersion.builder().slnid(mainSlnid).validFrom(LocalDate.now()).validTo(LocalDate.now()).id(1L).build();
    when(lineService.findLineVersions(mainSlnid)).thenReturn(List.of(lineVersion));
    //when
    LineVersion result = sublineService.getMainLineVersion(mainSlnid);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldReturnMainLineVersionWhenLineVersionToday() {
    //given
    String mainSlnid = "ch:1:slnid:8000";
    LocalDate today = LocalDate.now();
    LineVersion lineVersion =
        LineVersion.builder().slnid(mainSlnid).validFrom(today).validTo(today).id(1L).build();
    LineVersion lineVersion1 =
        LineVersion.builder().slnid(mainSlnid).validFrom(today.plusDays(30)).validTo(today.plusDays(30)).id(2L).build();
    List<LineVersion> versions = new java.util.ArrayList<>(List.of(lineVersion, lineVersion1));
    versions.sort(Comparator.comparing(LineVersion::getValidFrom));
    when(lineService.findLineVersions(mainSlnid)).thenReturn(versions);
    //when
    LineVersion result = sublineService.getMainLineVersion(mainSlnid);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(lineVersion.getId());
  }

  @Test
  void shouldReturnMainLineVersionWhenLineVersionIsFuture() {
    //given
    String mainSlnid = "ch:1:slnid:8000";
    LocalDate today = LocalDate.now();
    LineVersion lineVersion =
        LineVersion.builder().slnid(mainSlnid).validFrom(today.plusDays(100)).validTo(today.plusDays(100)).id(1L).build();
    LineVersion lineVersion1 =
        LineVersion.builder().slnid(mainSlnid).validFrom(today.plusDays(30)).validTo(today.plusDays(30)).id(2L).build();
    List<LineVersion> versions = new java.util.ArrayList<>(List.of(lineVersion, lineVersion1));
    versions.sort(Comparator.comparing(LineVersion::getValidFrom));
    when(lineService.findLineVersions(mainSlnid)).thenReturn(versions);
    //when
    LineVersion result = sublineService.getMainLineVersion(mainSlnid);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(lineVersion1.getId());
  }

  @Test
  void shouldReturnMainLineVersionWhenLineVersionIsPast() {
    //given
    String mainSlnid = "ch:1:slnid:8000";
    LocalDate today = LocalDate.now();
    LineVersion lineVersion =
        LineVersion.builder().slnid(mainSlnid).validFrom(today.minusDays(100)).validTo(today.minusDays(100)).id(1L).build();
    LineVersion lineVersion1 =
        LineVersion.builder().slnid(mainSlnid).validFrom(today.minusDays(30)).validTo(today.minusDays(30)).id(1L).build();
    List<LineVersion> versions = new java.util.ArrayList<>(List.of(lineVersion, lineVersion1));
    versions.sort(Comparator.comparing(LineVersion::getValidFrom));
    when(lineService.findLineVersions(mainSlnid)).thenReturn(versions);
    //when
    LineVersion result = sublineService.getMainLineVersion(mainSlnid);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(lineVersion1.getId());
  }

  @Test
  void shouldThrowExceptionWhenNoMainLineVersionMatch() {
    //given
    when(lineService.findLineVersions(any())).thenReturn(new ArrayList<>());
    //when && then
    assertThrows(NoSuchElementException.class, () -> sublineService.getMainLineVersion(any()));
  }

  @Test
  void shouldThrowExceptionWhenSublineValidityIsNotContainedInMainlineValidity() {
    String mainSlnid = "ch:1:slnid:8000";
    LocalDate today = LocalDate.now();

    LineVersion lineVersion =
        LineVersion.builder().slnid(mainSlnid).validFrom(today).validTo(today.plusDays(10)).id(1L).build();

    when(lineService.findLineVersions(mainSlnid)).thenReturn(List.of(lineVersion));

    SublineVersion sublineVersion =
        SublineVersion.builder().mainlineSlnid(mainSlnid).validFrom(today.minusDays(1000)).validTo(today.plusDays(1000)).build();

    assertThrows(DateRangeConflictException.class, () -> sublineService.validateSublineValidity(sublineVersion));
  }

  @Test
  void shouldNotThrowExceptionWhenSublineValidityIsNotContainedInMainlineValidity() {
    String mainSlnid = "ch:1:slnid:8000";

    LineVersion lineVersion =
        LineVersion.builder().slnid(mainSlnid).validFrom(LocalDate.of(2020, 1, 1)).validTo(LocalDate.of(2024, 1, 1)).id(1L)
            .build();

    when(lineService.findLineVersions(mainSlnid)).thenReturn(List.of(lineVersion));

    SublineVersion sublineVersion =
        SublineVersion.builder().mainlineSlnid(mainSlnid).validFrom(LocalDate.of(2022, 1, 1)).validTo(LocalDate.of(2023, 1, 1))
            .build();

    assertDoesNotThrow(() -> sublineService.validateSublineValidity(sublineVersion));
  }
}
