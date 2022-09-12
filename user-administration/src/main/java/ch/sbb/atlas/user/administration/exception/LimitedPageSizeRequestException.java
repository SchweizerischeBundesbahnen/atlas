package ch.sbb.atlas.user.administration.exception;

import ch.sbb.atlas.base.service.model.api.ErrorResponse;
import ch.sbb.atlas.base.service.model.exception.AtlasException;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LimitedPageSizeRequestException extends AtlasException {

    private static final String ERROR = "Page Request not allowed";

    private final int requestedPageSize;
    private final int maxAllowedPageSize;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(405)
            .message("Page size %s is bigger than max allowed page size %s".formatted(
                requestedPageSize, maxAllowedPageSize))
            .error(ERROR)
            .details(new TreeSet<>())
            .build();
    }

}
