package ch.sbb.atlas.user.administration.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.user.administration.entity.UserPermission.Fields;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class UserPermissionConflictException extends AtlasException {

    private static final String CODE_PREFIX = "USER_ADMIN.CONFLICT.";
    private static final String ERROR = "User Permission Conflict";

    private final String sbbUserId;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("A conflict occurred on UserPermission")
                .error(ERROR)
                .details(getErrorDetails())
                .build();
    }

    private SortedSet<Detail> getErrorDetails() {
        SortedSet<Detail> details = new TreeSet<>();

        details.add(Detail.builder()
                .field(Fields.sbbUserId)
                .message("Permissions for user {0} already existing")
                .displayInfo(
                        DisplayInfo.builder()
                                .code(CODE_PREFIX + Fields.sbbUserId.toUpperCase())
                                .with(Fields.sbbUserId, sbbUserId)
                                .build()
                ).build());

        return details;
    }

}
