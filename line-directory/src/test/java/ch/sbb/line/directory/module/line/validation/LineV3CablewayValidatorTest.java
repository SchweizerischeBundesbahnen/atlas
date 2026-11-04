package ch.sbb.line.directory.module.line.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3CablewayFieldNotAllowedException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class LineV3CablewayValidatorTest {

  private final LineV3CablewayValidator cablewayValidator = new LineV3CablewayValidator();

  @ParameterizedTest
  @EnumSource(value = LineMeanOfTransport.class, names = {"CABLE_RAILWAY", "AERIAL_TRAMWAY", "GONDOLA_LIFT", "CHAIRLIFT"})
  void shouldAcceptCablewayFieldsForCablewayMeansOfTransport(LineMeanOfTransport meanOfTransport) {
    // Given
    LineVersionV3 version = LineVersionV3TestData.cablewayBuilder().meansOfTransport(Set.of(meanOfTransport)).build();

    // When & Then
    assertThatCode(() -> cablewayValidator.validateCablewayFields(version)).doesNotThrowAnyException();
  }

  @ParameterizedTest
  @EnumSource(value = LineMeanOfTransport.class, mode = Mode.EXCLUDE,
      names = {"CABLE_RAILWAY", "AERIAL_TRAMWAY", "GONDOLA_LIFT", "CHAIRLIFT"})
  void shouldRejectCablewayFieldsForNonCablewayMeansOfTransport(LineMeanOfTransport meanOfTransport) {
    // Given
    LineVersionV3 version = LineVersionV3TestData.cablewayBuilder().meansOfTransport(Set.of(meanOfTransport)).build();

    // When
    Throwable thrown = catchThrowable(() -> cablewayValidator.validateCablewayFields(version));

    // Then
    assertThat(thrown).isInstanceOf(LineV3CablewayFieldNotAllowedException.class);
    assertThat(((LineV3CablewayFieldNotAllowedException) thrown).getFields())
        .containsExactlyInAnyOrder("cablewayNumber", "cablewayType", "cablewayName");
  }

  @Test
  void shouldAcceptCablewayFieldsWhenOnlyOneOfSeveralMeansOfTransportIsCableway() {
    // Given
    LineVersionV3 version = LineVersionV3TestData.cablewayBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS, LineMeanOfTransport.GONDOLA_LIFT))
        .build();

    // When & Then
    assertThatCode(() -> cablewayValidator.validateCablewayFields(version)).doesNotThrowAnyException();
  }

  @Test
  void shouldReportOnlyTheCablewayFieldsThatAreSet() {
    // Given
    LineVersionV3 version = LineVersionV3TestData.cablewayBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .cablewayNumber(null)
        .cablewayName("  ")
        .build();

    // When
    Throwable thrown = catchThrowable(() -> cablewayValidator.validateCablewayFields(version));

    // Then
    assertThat(((LineV3CablewayFieldNotAllowedException) thrown).getFields()).containsExactly("cablewayType");
  }

  @Test
  void shouldAcceptVersionWithoutCablewayFields() {
    // Given
    LineVersionV3 version = LineVersionV3TestData.line();

    // When & Then
    assertThatCode(() -> cablewayValidator.validateCablewayFields(version)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectCablewayFieldsWhenMeansOfTransportIsNull() {
    // Given
    LineVersionV3 version = LineVersionV3TestData.cablewayBuilder().meansOfTransport(null).build();

    // When
    Throwable thrown = catchThrowable(() -> cablewayValidator.validateCablewayFields(version));

    // Then
    assertThat(thrown).isInstanceOf(LineV3CablewayFieldNotAllowedException.class);
  }

}
