package ch.sbb.atlas.user.administration.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LimitedPageSizeRequestException extends AtlasException {

    private static final String ERROR = "Page Request not allowed";

    private final int requestedPageSize;
    private final int maxAllowedPageSize;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Page size %s is bigger than max allowed page size %s".formatted(
                        requestedPageSize, maxAllowedPageSize))
                .error(ERROR)
                .details(new TreeSet<>())
                .build();
    }

}
