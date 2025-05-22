package ch.sbb.atlas.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import java.util.SortedSet;
import org.junit.jupiter.api.Test;

class BulkImportTerminateForbiddenExceptionTest {

  private final BulkImportTerminateForbiddenException exception = new BulkImportTerminateForbiddenException();

  @Test
  void shouldInformAboutUnavailableHeight() {
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(403);
    SortedSet<Detail> details = exception.getErrorResponse().getDetails();
    assertThat(details).hasSize(1);
    assertThat(details.iterator().next().getDisplayInfo().getCode()).isEqualTo(
        "BULK_IMPORT.ERROR.TERMINATE_FORBIDDEN");
  }
}
