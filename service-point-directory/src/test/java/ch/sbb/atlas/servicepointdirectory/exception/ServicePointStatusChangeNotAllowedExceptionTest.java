package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import org.junit.jupiter.api.Test;

class ServicePointStatusChangeNotAllowedExceptionTest {

  @Test
  void shouldDisplayErrorMessage(){
    // given
    ServicePointStatusChangeNotAllowedException exception = new ServicePointStatusChangeNotAllowedException(Status.DRAFT,Status.DRAFT);
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("ServicePoint Status cannot be changed from DRAFT to DRAFT!");
    assertThat(errorResponse.getError()).isEqualTo("Update status not allowed!");
  }

}