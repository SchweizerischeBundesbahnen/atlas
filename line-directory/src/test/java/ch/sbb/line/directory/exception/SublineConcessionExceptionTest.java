package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SublineConcessionExceptionTest {

  @Test
  void shouldHaveCorrectCode() {
    SublineConcessionException exception = new SublineConcessionException();
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "LIDI.SUBLINE.ERROR.CONCESSION");
  }

}