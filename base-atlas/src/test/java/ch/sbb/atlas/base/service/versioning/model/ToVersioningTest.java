package ch.sbb.atlas.base.service.versioning.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class ToVersioningTest {

  @Test
  public void shouldThrowVersioningExceptionWhenCallGetValidFromAndVersionableIsNull() {
    //given
    ThrowingCallable buildToVersioning = () -> ToVersioning.builder().build();
    //then
    assertThatThrownBy(buildToVersioning).isInstanceOf(VersioningException.class)
                                         .hasMessageContaining(
                                             "Versionable object is null.");
  }

}