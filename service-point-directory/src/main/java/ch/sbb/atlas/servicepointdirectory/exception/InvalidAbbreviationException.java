package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class InvalidAbbreviationException extends AtlasException {

    private static final String ABBREVIATION_NOT_UNIQUE = "The abbreviation must be between 2 and 6 characters long, be in uppercase, and be unique";
    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ABBREVIATION_NOT_UNIQUE)
            .error(ABBREVIATION_NOT_UNIQUE)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
            .message(ABBREVIATION_NOT_UNIQUE)
            .displayInfo(builder()
                .code("SEPODI.SERVICE_POINTS.ABBREVIATION_NOT_UNIQUE")
                .build())
            .build());
    }
}
