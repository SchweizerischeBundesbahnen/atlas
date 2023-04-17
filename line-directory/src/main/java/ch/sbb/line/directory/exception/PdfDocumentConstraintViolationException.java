package ch.sbb.line.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class PdfDocumentConstraintViolationException extends AtlasException {

    private static final String ERROR = "Upload PDFs error";

    private final String message;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(message)
            .error(ERROR)
            .build()
            ;
    }
}
