package ch.sbb.atlas.user.administration.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RestrictionWithoutTypeExceptionTest {

  private final RestrictionWithoutTypeException restrictionWithoutTypeException = new RestrictionWithoutTypeException();

  @Test
  void shouldBeGoodErrorResponse() {
    assertThat(restrictionWithoutTypeException.getErrorResponse()).isNotNull();
    assertThat(restrictionWithoutTypeException.getErrorResponse().getStatus()).isEqualTo(400);
  }
}