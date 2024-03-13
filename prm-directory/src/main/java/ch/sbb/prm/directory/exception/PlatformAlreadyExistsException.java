package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class PlatformAlreadyExistsException extends AtlasException {
    private final String sloid;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("The platform with sloid " + getSloid() + " already exists.")
                .build();
    }
}