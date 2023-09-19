package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.SublineVersionBuilder;
import ch.sbb.line.directory.model.search.SublineSearchRestrictions;
import ch.sbb.line.directory.repository.SublineRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.SublineValidationService;
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
import org.springframework.web.server.ResponseStatusException;

class SublineServiceTest {

  private static final long ID = 1L;

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private SublineRepository sublineRepository;

  @Mock
  private LineService lineService;

  @Mock
  private VersionableService versionableService;

  @Mock
  private Specification<Subline> sublineSpecification;

  @Mock
  private SublineValidationService sublineValidationService;

  @Mock
  private CoverageService coverageService;

  private SublineService sublineService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineService = new SublineService(sublineVersionRepository, sublineRepository,
        versionableService, lineService, sublineValidationService, coverageService);
  }

  @Test
  void shouldGetPagableSublinesFromRepository() {
    // Given
    when(sublineSpecification.and(any())).thenReturn(sublineSpecification);
    Pageable pageable = Pageable.unpaged();

    // When
    sublineService.findAll(SublineSearchRestrictions.builder().pageable(pageable).build());

    // Then
    verify(sublineRepository).findAll(ArgumentMatchers.<Specification<Subline>>any(), eq(pageable));
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
  void shouldNotSaveSublineWithInvalidMainline() {
    // Given
    when(sublineVersionRepository.save(any())).thenAnswer(
        i -> i.getArgument(0, SublineVersion.class));
    when(sublineVersionRepository.findSwissLineNumberOverlaps(any())).thenReturn(
        Collections.emptyList());
    when(lineService.findLineVersions(any())).thenReturn(Collections.emptyList());
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    // When

    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
            () -> sublineService.save(sublineVersion))
        .withMessage(
            "400 BAD_REQUEST \"Main line with SLNID ch:1:slnid:1000546 does not exist\"");

    // Then
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
    assertThrows(StaleObjectStateException.class, executable);
    //then
    verify(sublineVersionRepository).incrementVersion("slnid");
  }
}
