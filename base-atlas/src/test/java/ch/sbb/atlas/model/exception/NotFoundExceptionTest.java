package ch.sbb.atlas.model.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotFoundExceptionTest {

  @Test
  public void shouldGetNotFoundException() {
    //given

    //when
    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      throw new NotFoundException.IdNotFoundException(123L);
    });

    //then
    org.assertj.core.api.Assertions.assertThat(exception.getErrorResponse().getError()).isEqualTo("Not found");
    org.assertj.core.api.Assertions.assertThat(exception.getErrorResponse().getStatus()).isEqualTo(404);
    org.assertj.core.api.Assertions.assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Entity not found");

  }

}