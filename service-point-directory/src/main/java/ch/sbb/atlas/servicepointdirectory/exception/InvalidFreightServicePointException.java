package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class InvalidFreightServicePointException extends AtlasException {

    private static final String ERROR_MESSAGE = "FreightServicePoint in CH needs sortCodeOfDestinationStation if validFrom is "
        + "at least today!";

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.PRECONDITION_FAILED.value())
            .message(ERROR_MESSAGE)
            .error(ERROR_MESSAGE)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
                .message(ERROR_MESSAGE)
                .displayInfo(builder()
                        .code("SEPODI.SERVICE_POINTS.ERROR.INVALID_FREIGHT_SERVICE_POINT")
                        .build())
                .build());
    }

}