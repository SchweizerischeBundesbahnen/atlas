package ch.sbb.prm.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BulkPlatformUpdateValidationExceptionTest {

  @Test
  void shouldDisplayReadableMessageReduced() {
    BulkPlatformUpdateValidationException bulkPlatformUpdateValidationException = new BulkPlatformUpdateValidationException(true,
        "ch:1:sloid:7000:1");
    assertThat(bulkPlatformUpdateValidationException.getErrorResponse().getMessage()).isEqualTo(
        "Attempting to save a Reduced object with wrong properties population!");
    assertThat(bulkPlatformUpdateValidationException.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode())
        .isEqualTo("BULK_IMPORT.ERROR.PRM.BAD_REQUEST_REDUCED");
  }

  @Test
  void shouldDisplayReadableMessageComplete() {
    BulkPlatformUpdateValidationException bulkPlatformUpdateValidationException = new BulkPlatformUpdateValidationException(false,
        "ch:1:sloid:7000:1");
    assertThat(bulkPlatformUpdateValidationException.getErrorResponse().getMessage()).isEqualTo(
        "Attempting to save a Complete object with wrong properties population!");
    assertThat(bulkPlatformUpdateValidationException.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode())
        .isEqualTo("BULK_IMPORT.ERROR.PRM.BAD_REQUEST_COMPLETE");
  }
}
