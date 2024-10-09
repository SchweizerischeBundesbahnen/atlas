package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServicePointStatusRevokedChangeNotAllowedExceptionTest {
    @Test
    void shouldDisplayErrorMessage(){
        // given
        ServicePointStatusRevokedChangeNotAllowedException exception = new ServicePointStatusRevokedChangeNotAllowedException(new ServicePointNumber(12345678), Status.REVOKED);
        // when & then
        ErrorResponse errorResponse = exception.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(412);
        assertThat(errorResponse.getMessage()).isEqualTo("ServicePoint Status cannot be changed for Status REVOKED and can be updated only from DRAFT to VALIDATED!");
        assertThat(errorResponse.getError()).isEqualTo("Trying to update status for ServicePointNumber 1234567 and current status: REVOKED");
        assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo()
                .getCode()).isEqualTo("SEPODI.SERVICE_POINTS.ERROR.STATUS_CHANGE_NOT_POSSIBLE");
    }
}
