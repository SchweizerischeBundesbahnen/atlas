package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidAbbreviationExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    InvalidAbbreviationException invalidAbbreviationException = new InvalidAbbreviationException();

    assertThat(
        invalidAbbreviationException.getErrorResponse().getDetails().iterator().next().getDisplayInfo().getCode())
        .isEqualTo("SEPODI.SERVICE_POINTS.ABBREVIATION_NOT_UNIQUE");
  }
}