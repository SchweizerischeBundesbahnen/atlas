package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SublineConcessionSwissSublineNumberExceptionTest {

  @Test
  void shouldHaveDisplayCode() {
    SublineConcessionSwissSublineNumberException exception = new SublineConcessionSwissSublineNumberException();
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.SUBLINE.ERROR.SWISS_SUBLINE_NUMBER");
  }
}