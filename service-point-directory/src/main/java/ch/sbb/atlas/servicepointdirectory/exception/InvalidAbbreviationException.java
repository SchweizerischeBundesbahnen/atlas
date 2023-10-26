package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import org.springframework.http.HttpStatus;

public class InvalidAbbreviationException extends AtlasException {

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("The abbreviation must be between 2 and 6 characters long, be in uppercase, and be unique.")
            .build();
    }
}
