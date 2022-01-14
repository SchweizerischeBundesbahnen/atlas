package ch.sbb.line.directory.validation;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.ConflictExcpetion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LineValidationTest {

  private final LineValidation lineValidation = new LineValidation();

  @Test
  void shouldNotSaveTemporaryLineWithValidityGreaterThan12Months() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 10, 15))
        .validTo(LocalDate.of(2022, 10, 16)).build();
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineValidation.validateTemporaryLines(lineVersion, List.of()));
  }

  @Test
  void shouldSaveTemporaryLine() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2022, 10, 1)).build();
    // When
    lineValidation.validateTemporaryLines(lineVersion, List.of());
    // Then
  }

  @Test
  void shouldNotSaveWith2ExistentTemporaryVersionsWhichAffectIncomingVersionWhenValidityLongerThan12() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).id(1L)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).id(2L)
            .validFrom(LocalDate.of(2021, 9, 1))
            .validTo(LocalDate.of(2022, 2, 1)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 8, 31)).build();
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineValidation.validateTemporaryLines(lineVersion, versions));
  }

  @Test
  void shouldSaveTemporaryLineWith1ExistentTemporaryVersionWhichAffectsWhenValidityIsLessThan12() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 7, 31)).build();
    // When
    lineValidation.validateTemporaryLines(lineVersion, versions);
    // Then
  }

  @Test
  void shouldThrowExceptionOn2TemporaryNew1NotTemporary() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).id(1L).validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).id(2L).validFrom(LocalDate.of(2021, 4, 1))
            .validTo(LocalDate.of(2021, 8, 31)).build(),
        LineTestData.lineVersionBuilder().id(3L).validFrom(LocalDate.of(2022, 3, 1))
            .validTo(LocalDate.of(2022, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2022, 2, 28)).build();
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineValidation.validateTemporaryLines(lineVersion, versions));
  }

  @Test
  void shouldSaveOn2TemporaryNew1NotTemporary() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().id(2L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
            .validTo(LocalDate.of(2021, 8, 31)).build(),
        LineTestData.lineVersionBuilder().id(3L).validFrom(LocalDate.of(2021, 10, 1))
            .validTo(LocalDate.of(2022, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2021, 9, 30)).build();
    // When
    lineValidation.validateTemporaryLines(lineVersion, versions);
    // Then
  }

  @Test
  void shouldWorkOn1TemporaryNewWithGap() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 2))
        .validTo(LocalDate.of(2021, 8, 31)).build();
    // When
    lineValidation.validateTemporaryLines(lineVersion, versions);
    // Then
  }

  @Test
  void shouldThrowExceptionOnUpdateWhenTemporaryVersionLongerThan12Months() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().id(2L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 8, 1))
            .validTo(LocalDate.of(2021, 8, 31)).build());
    LineVersion lineVersion = versions.get(1);
    lineVersion.setValidFrom(LocalDate.of(2021, 4, 1));
    lineVersion.setValidTo(LocalDate.of(2022, 2, 1));
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineValidation.validateTemporaryLines(lineVersion, versions));
  }

}
