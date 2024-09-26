package ch.sbb.atlas.versioning.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;

public class VersioningNoChangesException extends AtlasException {

    private static final String NO_ENTITIES_WERE_MODIFIED_MESSAGE = "No entities were modified after versioning execution.";

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS)
            .error(NO_ENTITIES_WERE_MODIFIED_MESSAGE)
            .message(NO_ENTITIES_WERE_MODIFIED_MESSAGE)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
            .message(NO_ENTITIES_WERE_MODIFIED_MESSAGE)
            .displayInfo(builder()
                .code("ERROR.WARNING.VERSIONING_NO_CHANGES")
                .build())
            .build());
    }
}
