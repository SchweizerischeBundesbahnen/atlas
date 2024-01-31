package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
@Getter
public class ServicePointStatusChangeNotAllowedException extends AtlasException {

    private final ServicePointNumber servicePointNumber;
    private final Status servicePointStatus;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.PRECONDITION_FAILED.value())
                .message("ServicePoint Status can be updated only from DRAFT to VALIDATED!")
                .error("Trying to update status to VALIDATED for the ServicePointNumber " +
                        servicePointNumber.getNumber() + " and current status: " + servicePointStatus)
                .build();
    }

}