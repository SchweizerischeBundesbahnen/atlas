package ch.sbb.line.directory.validation;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
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
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(() -> lineValidation.validateTemporaryLinesDuration(lineVersion, List.of()));
  }

  @Test
  void shouldSaveTemporaryLine() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2022, 10, 1)).build();
    // When
    lineValidation.validateTemporaryLinesDuration(lineVersion, List.of());
    // Then
  }

  @Test
  void shouldNotSaveWith2ExistentTemporaryVersionsWhichRelateIncomingVersionWhenValidityLongerThan12() {
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
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(() -> lineValidation.validateTemporaryLinesDuration(lineVersion, versions));
  }

  @Test
  void shouldSaveTemporaryLineWith1ExistentTemporaryVersionWhichRelatesWhenValidityIsLessThan12() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 7, 31)).build();
    // When
    lineValidation.validateTemporaryLinesDuration(lineVersion, versions);
    // Then
  }

  @Test
  void shouldThrowExceptionOn2RelatingTemporaryAnd1RelatingNotTemporary() {
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
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(() -> lineValidation.validateTemporaryLinesDuration(lineVersion, versions));
  }

  @Test
  void shouldSaveOn2RelatingTemporaryAnd1RelatingNotTemporary() {
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
    lineValidation.validateTemporaryLinesDuration(lineVersion, versions);
    // Then
  }

  @Test
  void shouldSaveOn1ExistingTemporaryWithGap() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 2))
        .validTo(LocalDate.of(2021, 8, 31)).build();
    // When
    lineValidation.validateTemporaryLinesDuration(lineVersion, versions);
    // Then
  }

  @Test
  void shouldThrowExceptionOnUpdateWhenRelatingTemporaryVersionsLongerThan12Months() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 3, 31)).build(),
        LineTestData.lineVersionBuilder().id(2L).type(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 8, 1))
            .validTo(LocalDate.of(2021, 8, 31)).build());
    LineVersion lineVersion = versions.get(1);
    lineVersion.setValidFrom(LocalDate.of(2021, 4, 1));
    lineVersion.setValidTo(LocalDate.of(2022, 2, 1));
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(() -> lineValidation.validateTemporaryLinesDuration(lineVersion, versions));
  }

  @Test
  void shouldThrowExceptionOn2VersionsGap1VersionAndNewVersion() {
    // Given
    List<LineVersion> versions = List.of(
        LineTestData.lineVersionBuilder().id(1L).type(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 4, 1)).build(),
        LineTestData.lineVersionBuilder().id(2L).type(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 4, 2))
            .validTo(LocalDate.of(2021, 9, 2)).build(),
        LineTestData.lineVersionBuilder().id(3L).type(LineType.TEMPORARY)
            .validFrom(LocalDate.of(2021, 10, 2))
            .validTo(LocalDate.of(2022, 2, 2)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2022, 2, 3))
        .validTo(LocalDate.of(2022, 11, 3)).build();
    // When
    assertThatExceptionOfType(TemporaryLineValidationException.class).isThrownBy(() -> lineValidation.validateTemporaryLinesDuration(lineVersion, versions));
  }

}
