package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class HeightNotCalculatableException extends AtlasException {

    private static final String ERROR = "The Swiss-Topo endpoint is currently unavailable.";

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ERROR)
            .error(ERROR)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
            .message(ERROR)
            .displayInfo(builder()
                .code("SEPODI.SERVICE_POINTS.HEIGHT_NOT_CALCULABLE")
                .build())
            .build());
    }
}
