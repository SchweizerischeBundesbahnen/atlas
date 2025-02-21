package ch.sbb.line.directory.validation;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.LineTypeOrderlyException;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineValidationServiceTest {

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private CoverageValidationService coverageValidationService;

  @Mock
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private LineValidationService lineValidationService;

  @BeforeEach()
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineValidationService = new LineValidationService(lineVersionRepository,
        coverageValidationService, sharedBusinessOrganisationService);
  }

  @Test
  void shouldThrowLineConflictExceptionWhenFoundSwissLineNumberOverlaps() {
    //given
    LineVersion lineVersion = LineTestData.lineVersion();
    when(lineVersionRepository.findSwissLineNumberOverlaps(lineVersion)).thenReturn(
        List.of(lineVersion));

    //when
    assertThatExceptionOfType(LineConflictException.class).isThrownBy(
        () -> lineValidationService.validateLinePreconditionBusinessRule(lineVersion));
  }

  @Test
  void shouldNotSaveTemporaryLineWithValidityGreaterThan12Months() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 10, 15))
        .validTo(LocalDate.of(2022, 10, 16))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        List.of());
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldSaveTemporaryLine() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2022, 10, 1))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        List.of());
    // When & Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldNotSaveWith2ExistentTemporaryVersionsWhichRelateIncomingVersionWhenValidityLongerThan12() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY).id(1L)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY).id(2L)
            .validFrom(LocalDate.of(2021, 9, 1))
            .validTo(LocalDate.of(2022, 2, 1)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 8, 31))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldSaveTemporaryLineWith1ExistentTemporaryVersionWhichRelatesWhenValidityIsLessThan12() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder()
            .id(1L)
            .lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31))
            .build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 7, 31))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When & Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldThrowExceptionOn2RelatingTemporaryAnd1RelatingNotTemporary() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder()
            .lineType(LineType.TEMPORARY)
            .id(1L)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31))
            .build(),
        LineTestData.lineVersionBuilder()
            .lineType(LineType.TEMPORARY)
            .id(2L)
            .validFrom(LocalDate.of(2021, 4, 1))
            .validTo(LocalDate.of(2021, 8, 31))
            .build(),
        LineTestData.lineVersionBuilder().id(3L).validFrom(LocalDate.of(2022, 3, 1))
            .validTo(LocalDate.of(2022, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2022, 2, 28))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldSaveOn2RelatingTemporaryAnd1RelatingNotTemporary() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder()
            .id(1L)
            .lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31))
            .build(),
        LineTestData.lineVersionBuilder()
            .id(2L)
            .lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 4, 1))
            .validTo(LocalDate.of(2021, 8, 31))
            .build(),
        LineTestData.lineVersionBuilder().id(3L).validFrom(LocalDate.of(2021, 10, 1))
            .validTo(LocalDate.of(2022, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2021, 9, 30))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When & Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldSaveOn1ExistingTemporaryWithGap() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder()
            .id(1L)
            .lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31))
            .build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 4, 2))
        .validTo(LocalDate.of(2021, 8, 31))
        .build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When & Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldThrowExceptionOnUpdateWhenRelatingTemporaryVersionsLongerThan12Months() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().id(2L).lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 8, 1))
            .validTo(LocalDate.of(2021, 8, 31)).build());
    LineVersion lineVersion = versions.get(1);
    lineVersion.setValidFrom(LocalDate.of(2021, 4, 1));
    lineVersion.setValidTo(LocalDate.of(2022, 2, 1));
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldThrowExceptionOn2VersionsGap1VersionAndNewVersion() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 4, 1)).build(),
        LineTestData.lineVersionBuilder().id(2L).lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 4, 2))
            .validTo(LocalDate.of(2021, 9, 2)).build(),
        LineTestData.lineVersionBuilder().id(3L).lineType(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 10, 2))
            .validTo(LocalDate.of(2022, 2, 2)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2022, 2, 3))
        .validTo(LocalDate.of(2022, 11, 3)).build();
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        versions);
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(lineVersion));
  }

  @Test
  void shouldThrowLineRangeSmallerThenSublineRangeExceptionWhenLineRangeIsRightSmallerThenSublineRange() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    sublineVersion.setValidTo(LocalDate.of(2000, 12, 31));
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(any())).thenReturn(
        sublineVersions);

    LineVersion lineVersion = LineTestData.lineVersion();
    lineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    lineVersion.setValidTo(LocalDate.of(2000, 12, 30));
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        lineVersions);

    // When
    lineValidationService.validateLineAfterVersioningBusinessRule(lineVersion);

    // then
    verify(coverageValidationService).validateLineSublineCoverage(lineVersion);
  }

  @Test
  void shouldThrowLineRangeSmallerThenSublineRangeExceptionWhenLineRangeIsSmallerThenSublineRange() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    sublineVersion.setValidFrom(LocalDate.of(2000, 1, 1));
    sublineVersion.setValidTo(LocalDate.of(2000, 12, 31));
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);

    when(sublineVersionRepository.getSublineVersionByMainlineSlnid(any())).thenReturn(
        sublineVersions);

    LineVersion lineVersion = LineTestData.lineVersion();
    lineVersion.setValidFrom(LocalDate.of(2000, 1, 2));
    lineVersion.setValidTo(LocalDate.of(2000, 12, 30));
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).thenReturn(
        lineVersions);

    // When
    lineValidationService.validateLineAfterVersioningBusinessRule(lineVersion);

    //then
    verify(coverageValidationService).validateLineSublineCoverage(lineVersion);
  }

  @Test
  void shouldNotValidateLineConflictWhenLineTypeIsOrderly() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).build();
    // When
    LineVersion lineVersionDb = LineTestData.lineVersionBuilder().swissLineNumber("IC2").lineType(LineType.ORDERLY).build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersionDb);

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersionDb.getSlnid())).thenReturn(
        lineVersions);
    // Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateLineConflict(lineVersion));

    verify(lineVersionRepository).findSwissLineNumberOverlaps(lineVersion);
  }

  @Test
  void shouldValidateLineConflictWhenLineTypeIsOrderly() {
    //given
    LineVersion lineVersion =
        LineTestData.lineVersionBuilder().validFrom(LocalDate.of(1900, 1, 1)).validTo(LocalDate.of(1900, 1, 1))
            .lineType(LineType.ORDERLY).build();
    // When
    LineVersion lineVersionDb = LineTestData.lineVersionBuilder().swissLineNumber("IC2").lineType(LineType.ORDERLY).build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersionDb);

    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersionDb.getSlnid())).thenReturn(
        lineVersions);
    // Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateLineConflict(lineVersion));

    verify(lineVersionRepository).findSwissLineNumberOverlaps(lineVersion);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldValidateLineConflictWhenLineTypeIsNotOrderly(LineType lineType) {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(lineType).build();
    // When
    LineVersion lineVersionDb = LineTestData.lineVersionBuilder().lineType(lineType).build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersionDb);
    when(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersionDb.getSlnid())).thenReturn(
        lineVersions);
    // Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateLineConflict(lineVersion));
    verify(lineVersionRepository, never()).findSwissLineNumberOverlaps(lineVersion);

  }

  @Test
  void shouldNotThrowLineTypeOrderlyExceptionWhenLineTypeIsOrderlyAndSwissLineNumberAndConcessionTypeAreNotNull() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).swissLineNumber("IC")
        .concessionType(LineConcessionType.LINE_ABROAD).build();

    //when and then
    assertThatNoException().isThrownBy(() -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @Test
  void shouldThrowLineTypeOrderlyExceptionWhenLineTypeIsOrderlyAndSwissLineNumberAndConcessionTypeAreNull() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).swissLineNumber(null)
        .concessionType(null).build();

    //when and then
    assertThrows(LineTypeOrderlyException.class, () -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @Test
  void shouldThrowLineTypeOrderlyExceptionWhenLineTypeIsOrderlyAndConcessionTypeIsNull() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).swissLineNumber("ICe")
        .concessionType(null).build();

    //when and then
    assertThrows(LineTypeOrderlyException.class, () -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @Test
  void shouldThrowLineTypeOrderlyExceptionWhenLineTypeIsOrderlyAndSwissLineNumberIsNull() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).swissLineNumber(null)
        .concessionType(LineConcessionType.LINE_ABROAD).build();

    //when and then
    assertThrows(LineTypeOrderlyException.class, () -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldNotThrowLineTypeOrderlyExceptionWhenLineTypeIsNotOrderlyAndSwissLineNumberAndConcessionTypeAreNull(
      LineType lineType) {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber(null)
        .concessionType(null).build();

    //when and then
    assertThatNoException().isThrownBy(() -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldThrowLineTypeOrderlyExceptionWhenLineTypeIsNotOrderlyAndSwissLineNumberAndConcessionTypeAreNotNull(
      LineType lineType) {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber("IC")
        .concessionType(LineConcessionType.CANTONALLY_APPROVED_LINE).build();

    //when and then
    assertThrows(LineTypeOrderlyException.class, () -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldThrowLineTypeOrderlyExceptionWhenLineTypeIsNotOrderlyAndConcessionTypeIsNotNull(LineType lineType) {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber(null)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION).build();

    //when and then
    assertThrows(LineTypeOrderlyException.class, () -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldThrowLineTypeOrderlyExceptionWhenLineTypeIsNotOrderlyAndSwissLineNumberIsNotNull(LineType lineType) {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(lineType).swissLineNumber("IC")
        .concessionType(null).build();

    //when and then
    assertThrows(LineTypeOrderlyException.class, () -> lineValidationService.dynamicBeanValidation(lineVersion));
  }

}
