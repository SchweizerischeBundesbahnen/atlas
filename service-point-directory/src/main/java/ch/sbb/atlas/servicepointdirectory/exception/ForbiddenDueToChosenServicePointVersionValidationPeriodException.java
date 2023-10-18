package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor
public class ForbiddenDueToChosenServicePointVersionValidationPeriodException extends AtlasException {

    private final ServicePointNumber servicePointNumber;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Operation not allowed")
                .error("The validation period of all versions of servicePointNumber: " + servicePointNumber.getNumber() +
                        " don't match with your own servicePointVersion validation period.")
                .details(new TreeSet<>(getErrorDetails()))
                .build();
    }

    private List<ErrorResponse.Detail> getErrorDetails() {
        return List.of(ErrorResponse.Detail.builder()
                .message("The validation period of all versions of servicePointNumber: " + servicePointNumber.getNumber() +
                        " don't match with your own servicePointVersion validation period. " +
                        "Please check validation period of chosen servicePointVersion.")
                .field(ServicePointVersion.Fields.number)
                .displayInfo(builder().code("TTH.NOTIFICATION.OPERATION_NOT_ALLOWED_DUE_TO_TTH_YEAR_SETTINGS").build())
                .build());
    }
}