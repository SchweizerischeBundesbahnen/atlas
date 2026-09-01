package ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;

class GlobalIdBulkImportInfoExceptionTest {

  private static final String GLOBAL_ID = "de:05770:1282";
  private static final ServicePointNumber DISPLACED = ServicePointNumber.ofNumberWithoutCheckDigit(1105771);

  @Test
  void shouldReportRepointingWithTheInfoStatusSoItIsNotLoggedAsAFailure() {
    // Given
    GlobalIdBulkImportInfoException exception = GlobalIdBulkImportInfoException.repointed(GLOBAL_ID, DISPLACED);

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS);
    assertThat(errorResponse.getError()).isEqualTo("Global-ID bulk import info");
  }

  @Test
  void shouldNameTheDisplacedServicePointInTheRepointingMessage() {
    // Given
    GlobalIdBulkImportInfoException exception = GlobalIdBulkImportInfoException.repointed(GLOBAL_ID, DISPLACED);

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Global-ID " + GLOBAL_ID + " was taken from service point " + DISPLACED.getNumber() + ".");
  }

  @Test
  void shouldExposeTheRepointedGlobalIdAndDisplacedNumberAsDisplayParameters() {
    // Given
    GlobalIdBulkImportInfoException exception = GlobalIdBulkImportInfoException.repointed(GLOBAL_ID, DISPLACED);

    // When
    Detail detail = exception.getErrorResponse().getDetails().first();

    // Then
    assertThat(detail.getField()).isEqualTo("globalId");
    assertThat(exception.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REPOINTED");
    assertThat(detail.getDisplayInfo().getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REPOINTED");
    assertThat(detail.getDisplayInfo().getParameters()).extracting(Parameter::getKey, Parameter::getValue)
        .containsExactlyInAnyOrder(tuple("globalId", GLOBAL_ID),
            tuple("servicePointNumber", String.valueOf(DISPLACED.getNumber())));
  }

  @Test
  void shouldReportRemovalWithTheInfoStatus() {
    // Given
    GlobalIdBulkImportInfoException exception = GlobalIdBulkImportInfoException.removed(GLOBAL_ID);

    // When
    ErrorResponse errorResponse = exception.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS);
    assertThat(errorResponse.getMessage()).isEqualTo("Global-ID " + GLOBAL_ID + " was removed.");
  }

  @Test
  void shouldExposeTheRemovedGlobalIdAsDisplayParameter() {
    // Given
    GlobalIdBulkImportInfoException exception = GlobalIdBulkImportInfoException.removed(GLOBAL_ID);

    // When
    Detail detail = exception.getErrorResponse().getDetails().first();

    // Then
    assertThat(exception.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REMOVED");
    assertThat(detail.getDisplayInfo().getParameters()).extracting(Parameter::getKey, Parameter::getValue)
        .containsExactly(tuple("globalId", GLOBAL_ID));
  }
}
