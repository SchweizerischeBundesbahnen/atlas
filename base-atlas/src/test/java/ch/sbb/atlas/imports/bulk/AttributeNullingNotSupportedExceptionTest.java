package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttributeNullingNotSupportedExceptionTest {

  @Test
  void shouldProvideMessageAndDisplayCode() {
    AttributeNullingNotSupportedException exception = new AttributeNullingNotSupportedException("sloid");

    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.ERROR.ATTRIBUTE_NULLING_NOT_SUPPORTED");
    assertThat(exception.getErrorResponse().getDetails().getFirst().getMessage()).isEqualTo("Setting attribute sloid to null is not supported");
  }
}