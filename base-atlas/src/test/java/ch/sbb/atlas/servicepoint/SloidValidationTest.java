package ch.sbb.atlas.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class SloidValidationTest {

  @Test
  void shouldReportValidSloidWhenServicePointIsInSwitzerlandEndingInEmpty() {
    boolean isValid = SloidValidation.isSloidValid("ch:1:sloid:7000::", SloidValidation.EXPECTED_COLONS_PLATFORM,
        ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReportValidSloidWhenServicePointIsInSwitzerlandEmptyAreaId() {
    boolean isValid = SloidValidation.isSloidValid("ch:1:sloid:7000::1", SloidValidation.EXPECTED_COLONS_PLATFORM,
        ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReportValidSloidWhenServicePointIsInGermanyBus() {
    boolean isValid = SloidValidation.isSloidValid("ch:1:sloid:1107000::1", SloidValidation.EXPECTED_COLONS_PLATFORM,
        ServicePointNumber.ofNumberWithoutCheckDigit(1107000));
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReportInvalidSloidForWrongPrefix() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch.1:sloid:7000::", SloidValidation.EXPECTED_COLONS_PLATFORM,
            ServicePointNumber.ofNumberWithoutCheckDigit(8507000)));
  }

  @Test
  void shouldReportInvalidSloidForWrongServicePointId() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch:1:sloid:8507000::", SloidValidation.EXPECTED_COLONS_PLATFORM,
            ServicePointNumber.ofNumberWithoutCheckDigit(8507000)));
  }

  @Test
  void shouldReportInvalidSloidForWrongServicePointNumber() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch:1:sloid:7500::1", SloidValidation.EXPECTED_COLONS_PLATFORM,
            ServicePointNumber.ofNumberWithoutCheckDigit(8507000)));
  }

  @Test
  void shouldReportInvalidSloidForWrongSid4ptChar() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch:1:sloid:7000::;", SloidValidation.EXPECTED_COLONS_PLATFORM,
            ServicePointNumber.ofNumberWithoutCheckDigit(8507000)));
  }
}