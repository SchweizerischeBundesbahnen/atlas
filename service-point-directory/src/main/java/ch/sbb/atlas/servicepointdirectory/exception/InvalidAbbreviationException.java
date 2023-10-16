package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import org.springframework.http.HttpStatus;

public class InvalidAbbreviationException extends AtlasException {

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Modification attempt detected: Abbreviations are immutable and cannot be updated or deleted.")
            .build();
    }
}
