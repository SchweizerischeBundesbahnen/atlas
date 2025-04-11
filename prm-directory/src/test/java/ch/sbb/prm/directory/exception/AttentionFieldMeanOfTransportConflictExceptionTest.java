package ch.sbb.prm.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttentionFieldMeanOfTransportConflictExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    AttentionFieldMeanOfTransportConflictException exception = new AttentionFieldMeanOfTransportConflictException();
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("PRM.PLATFORM.ATTENTION_FIELD_CONFLICT");
  }
}