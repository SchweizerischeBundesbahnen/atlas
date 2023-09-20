package ch.sbb.line.directory.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SubLineAssignToLineConflictException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class SublineValidationTest {

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private CoverageValidationService coverageValidationService;

  @Mock
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private SublineValidationService sublineValidationService;

  @BeforeEach()
   void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineValidationService = new SublineValidationService(sublineVersionRepository,
        lineVersionRepository, coverageValidationService, sharedBusinessOrganisationService);
  }

  @Test
  void shouldNotSaveWhenTryToAssignDifferentMainlineToALine() {
    //given
    LineVersion lineVersion = LineTestData.lineVersion();
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setId(123L);
    sublineVersion.setMainlineSlnid(lineVersion.getSwissLineNumber());
    SublineVersion sublineVersionMainLineChanged = SublineTestData.sublineVersion();
    sublineVersionMainLineChanged.setMainlineSlnid("changed");
    sublineVersionMainLineChanged.setId(123L);
    when(sublineVersionRepository.findById(anyLong())).thenReturn(
        Optional.of(sublineVersionMainLineChanged));

    //when
    assertThatExceptionOfType(SubLineAssignToLineConflictException.class).isThrownBy(
        () -> sublineValidationService.validateDifferentMainLineAssignRule(sublineVersion));
  }

  @Test
  void shouldNotSaveWhenSublineRangeIsLeftOutsideOfTheMainLine() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersion();
    firstLineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    firstLineVersion.setValidTo(LocalDate.of(2000, 12, 31));
    LineVersion secondLineVersion = LineTestData.lineVersion();
    secondLineVersion.setValidFrom(LocalDate.of(2001, 1, 1));
    secondLineVersion.setValidTo(LocalDate.of(2001, 12, 31));
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(any())).thenReturn(lineVersions);

    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setValidFrom(LocalDate.of(1999, 12, 31));
    sublineVersion.setValidTo(LocalDate.of(2001, 12, 31));

    //when
    boolean result = sublineValidationService.validateLineRangeRule(sublineVersion);

    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotSaveWhenSublineRangeIsRightOutsideOfTheMainLine() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersion();
    firstLineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    firstLineVersion.setValidTo(LocalDate.of(2000, 12, 31));
    LineVersion secondLineVersion = LineTestData.lineVersion();
    secondLineVersion.setValidFrom(LocalDate.of(2001, 1, 1));
    secondLineVersion.setValidTo(LocalDate.of(2001, 12, 31));
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(any())).thenReturn(lineVersions);

    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    sublineVersion.setValidTo(LocalDate.of(2002, 1, 1));

    //when
    boolean result = sublineValidationService.validateLineRangeRule(sublineVersion);

    //then
    assertThat(result).isTrue();
  }


  @Test
  void shouldNotSaveWhenSublineRangeIsOutsideOfTheMainLine() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersion();
    firstLineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    firstLineVersion.setValidTo(LocalDate.of(2000, 12, 31));
    LineVersion secondLineVersion = LineTestData.lineVersion();
    secondLineVersion.setValidFrom(LocalDate.of(2001, 1, 1));
    secondLineVersion.setValidTo(LocalDate.of(2001, 12, 31));
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(any())).thenReturn(lineVersions);

    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setValidFrom(LocalDate.of(1999, 12, 31));
    sublineVersion.setValidTo(LocalDate.of(2002, 1, 1));

    //when
    boolean result = sublineValidationService.validateLineRangeRule(sublineVersion);

    //then
    assertThat(result).isTrue();
  }

}
