package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class AbbreviationUpdateNotAllowedException extends AtlasException {

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .message("Modification attempt detected: Abbreviations are immutable and cannot be updated or deleted.")
            .build();
    }

}
