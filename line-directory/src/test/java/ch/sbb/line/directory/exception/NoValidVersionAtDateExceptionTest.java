package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class NoValidVersionAtDateExceptionTest {

  @Test
  void shouldDisplayExeptionCorrectly() {
    NoValidVersionAtDateException exception = new NoValidVersionAtDateException(LocalDate.of(2020, 1, 1), "ch:1:ttfnid:12341");
    assertThat(exception.getErrorResponse().getError()).isEqualTo("There is no version of ch:1:ttfnid:12341 valid at 01.01.2020");
  }

}