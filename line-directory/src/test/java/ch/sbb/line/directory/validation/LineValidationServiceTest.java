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
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.LineTypeOrderlyException;
import ch.sbb.line.directory.exception.OrderlyLineValidityException;
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
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private LineValidationService lineValidationService;

  @BeforeEach()
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineValidationService = new LineValidationService(lineVersionRepository, sharedBusinessOrganisationService);
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
  void shouldNotSaveTemporaryLineWithValidityGreaterThan14Days() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 10, 15))
        .validTo(LocalDate.of(2021, 10, 31))
        .build();
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(List.of(lineVersion)));
  }

  @Test
  void shouldNotSaveTemporaryLineWithValidity15Days() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 1, 15))
        .build();
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(List.of(lineVersion)));
  }

  @Test
  void shouldSaveTemporaryLine() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2021, 10, 14))
        .build();
    // When & Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateTemporaryLinesDuration(List.of(lineVersion)));
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

  @Test
  void shouldNotSaveOrderlyLineWithValidity14Days() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.ORDERLY)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 1, 14))
        .build();
    // When
    assertThatExceptionOfType(OrderlyLineValidityException.class).isThrownBy(
        () -> lineValidationService.validateOrderlyLinesDuration(List.of(lineVersion)));
  }

  @Test
  void shouldSaveOrderlyLineWithValidity15Days() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
        .lineType(LineType.ORDERLY)
        .validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2021, 10, 15))
        .build();
    // When & Then
    assertThatNoException().isThrownBy(
        () -> lineValidationService.validateOrderlyLinesDuration(List.of(lineVersion)));
  }

}
