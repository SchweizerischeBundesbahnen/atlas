package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import org.junit.jupiter.api.Test;

class InvalidGlobalIdExceptionTest {

  @Test
  void shouldRejectAGlobalIdWithTheWrongCountryPrefix() {
    // Given
    InvalidGlobalIdException exception = InvalidGlobalIdException.countryMismatch("de:", "at:47:1234");

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("Global-ID 'at:47:1234' must start with 'de:' for this country.");
    assertThat(exception.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.COUNTRY_MISMATCH");
  }

  @Test
  void shouldExposeTheExpectedPrefixAsDisplayParameter() {
    // Given
    InvalidGlobalIdException exception = InvalidGlobalIdException.countryMismatch("de:", "at:47:1234");

    // When
    Detail detail = exception.getErrorResponse().getDetails().first();

    // Then
    assertThat(detail.getField()).isEqualTo("globalId");
    assertThat(detail.getDisplayInfo().getParameters()).extracting(Parameter::getKey, Parameter::getValue)
        .containsExactly(tuple("prefix", "de:"));
  }

  @Test
  void shouldRejectAGlobalIdForACountryThatDoesNotSupportIt() {
    // Given
    InvalidGlobalIdException exception = InvalidGlobalIdException.notAllowedForCountry();

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "A Global-ID can only be entered for German (11, 80) or Austrian (12, 81) stopPoints.");
    assertThat(exception.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.NOT_ALLOWED_FOR_COUNTRY");
  }

  @Test
  void shouldReportAGlobalIdTakenByAnotherStopAsConflict() {
    // Given
    InvalidGlobalIdException exception = InvalidGlobalIdException.alreadyUsed(new GlobalId("de:05770:1282"));

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(409);
    assertThat(errorResponse.getMessage()).isEqualTo("Global-ID 'de:05770:1282' is already used by another stopPoint.");
    assertThat(exception.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.ALREADY_USED");
  }

  @Test
  void shouldExposeTheConflictingGlobalIdAsDisplayParameter() {
    // Given
    InvalidGlobalIdException exception = InvalidGlobalIdException.alreadyUsed(new GlobalId("de:05770:1282"));

    // When
    Detail detail = exception.getErrorResponse().getDetails().first();

    // Then
    assertThat(detail.getDisplayInfo().getParameters()).extracting(Parameter::getKey, Parameter::getValue)
        .containsExactly(tuple("globalId", "de:05770:1282"));
  }

  @Test
  void shouldRefuseAnEmptyGlobalIdAndPointAtTheExplicitNullingValue() {
    // Given
    InvalidGlobalIdException exception = InvalidGlobalIdException.empty();

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("The globalId is empty. Use <null> to remove an assigned Global-ID.");
    assertThat(exception.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.EMPTY");
  }
}
