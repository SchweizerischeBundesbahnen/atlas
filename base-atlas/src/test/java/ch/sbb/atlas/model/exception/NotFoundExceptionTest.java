package ch.sbb.atlas.model.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

  @Test
  void shouldPreserveCauseAndErrorResponse() {
    IllegalStateException cause = new IllegalStateException("Upstream entity not found");

    NotFoundException exception = new NotFoundException("id", "123", cause);

    assertThat(exception).hasCause(cause);
    assertThat(exception.getErrorResponse()).usingRecursiveComparison()
        .isEqualTo(new NotFoundException.IdNotFoundException(123L).getErrorResponse());
  }

  @Test
  void shouldGetNotFoundException() {
    //given

    //when
    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      throw new NotFoundException.IdNotFoundException(123L);
    });

    //then
    assertThat(exception.getErrorResponse().getError()).isEqualTo("Not found");
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(404);
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Entity not found");

  }

}