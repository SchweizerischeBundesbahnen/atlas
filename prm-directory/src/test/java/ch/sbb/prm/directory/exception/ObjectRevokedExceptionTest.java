package ch.sbb.prm.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.prm.directory.entity.ReferencePointVersion;
import org.junit.jupiter.api.Test;

class ObjectRevokedExceptionTest {

  @Test
  void shouldDisplayReadableMessage() {
    ObjectRevokedException objectRevokedException = new ObjectRevokedException(ReferencePointVersion.class, "ch:1:sloid:7000:1");
    assertThat(objectRevokedException.getErrorResponse().getMessage()).isEqualTo("The ReferencePointVersion with sloid "
        + "ch:1:sloid:7000:1 is revoked. Updates are not allowed.");
  }
}