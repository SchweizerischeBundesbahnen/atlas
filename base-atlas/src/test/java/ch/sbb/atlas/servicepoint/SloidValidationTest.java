package ch.sbb.atlas.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class SloidValidationTest {

  private static final ServicePointNumber SWISS_SERVICE_POINT_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(8507000);
  private static final ServicePointNumber GERMANY_BUS_SERVICE_POINT_NUMBER =
      ServicePointNumber.ofNumberWithoutCheckDigit(1107000);

  @Test
  void shouldReportValidSloidWhenServicePointIsInSwitzerlandEndingInEmpty() {
    boolean isValid = SloidValidation.isSloidValid("ch:1:sloid:7000::", SloidValidation.EXPECTED_COLONS_PLATFORM,
        SWISS_SERVICE_POINT_NUMBER);
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReportValidSloidWhenServicePointIsInSwitzerlandEmptyAreaId() {
    boolean isValid = SloidValidation.isSloidValid("ch:1:sloid:7000::1", SloidValidation.EXPECTED_COLONS_PLATFORM,
        SWISS_SERVICE_POINT_NUMBER);
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReportValidSloidWhenServicePointIsInGermanyBus() {
    boolean isValid = SloidValidation.isSloidValid("ch:1:sloid:1107000::1", SloidValidation.EXPECTED_COLONS_PLATFORM,
        GERMANY_BUS_SERVICE_POINT_NUMBER);
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReportInvalidSloidForWrongPrefix() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch.1:sloid:7000::", SloidValidation.EXPECTED_COLONS_PLATFORM,
            SWISS_SERVICE_POINT_NUMBER));
  }

  @Test
  void shouldReportInvalidSloidForWrongServicePointId() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch:1:sloid:8507000::", SloidValidation.EXPECTED_COLONS_PLATFORM,
            SWISS_SERVICE_POINT_NUMBER));
  }

  @Test
  void shouldReportInvalidSloidForWrongServicePointNumber() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch:1:sloid:7500::1", SloidValidation.EXPECTED_COLONS_PLATFORM,
            SWISS_SERVICE_POINT_NUMBER));
  }

  @Test
  void shouldReportInvalidSloidForWrongSid4ptChar() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> SloidValidation.isSloidValid("ch:1:sloid:7000::;", SloidValidation.EXPECTED_COLONS_PLATFORM,
            SWISS_SERVICE_POINT_NUMBER));
  }
}