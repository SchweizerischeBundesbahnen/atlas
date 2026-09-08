package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import org.junit.jupiter.api.Test;

class ServicePointIdentifierMismatchExceptionTest {

  @Test
  void shouldRejectTheLineWithBadRequestRatherThanPickingOneIdentifier() {
    // Given
    ServicePointIdentifierMismatchException exception = new ServicePointIdentifierMismatchException("ch:1:sloid:5770",
        1105771);

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(400);
  }

  @Test
  void shouldNameBothConflictingIdentifiersInTheMessage() {
    // Given
    ServicePointIdentifierMismatchException exception = new ServicePointIdentifierMismatchException("ch:1:sloid:5770",
        1105771);

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Sloid ch:1:sloid:5770 does not belong to service point number 1105771.");
    assertThat(errorResponse.getError()).isEqualTo(errorResponse.getMessage());
  }

  @Test
  void shouldExposeBothIdentifiersAsDisplayParameters() {
    // Given
    ServicePointIdentifierMismatchException exception = new ServicePointIdentifierMismatchException("ch:1:sloid:5770",
        1105771);

    // When
    Detail detail = exception.getErrorResponse().getDetails().first();

    // Then
    assertThat(detail.getField()).isEqualTo("sloid");
    assertThat(detail.getDisplayInfo().getCode()).isEqualTo(
        "SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.SLOID_NUMBER_MISMATCH");
    assertThat(detail.getDisplayInfo().getParameters()).extracting(Parameter::getKey, Parameter::getValue)
        .containsExactlyInAnyOrder(tuple("sloid", "ch:1:sloid:5770"), tuple("number", "1105771"));
  }

  @Test
  void shouldKeepTheOffendingIdentifiersAccessibleOnTheException() {
    // Given
    ServicePointIdentifierMismatchException exception = new ServicePointIdentifierMismatchException("ch:1:sloid:5770",
        1105771);

    // When & Then
    assertThat(exception.getSloid()).isEqualTo("ch:1:sloid:5770");
    assertThat(exception.getNumber()).isEqualTo(1105771);
  }
}
