package ch.sbb.atlas.model.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

  @Test
  void shouldGetExportException() {
    //when
    BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> {
      throw new BadRequestException("BadRequestException is thrown.");
    });

    //then
    assertThat(exception.getErrorResponse().getError()).isEqualTo("BadRequestException is thrown.");
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(400);
    assertThat(exception.getErrorResponse().getMessage()).contains("BadRequestException is thrown.");
  }

}
