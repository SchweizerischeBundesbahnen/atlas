package ch.sbb.timetable.field.number.exceptions;

import ch.sbb.timetable.field.number.api.ErrorResponseModel;
import ch.sbb.timetable.field.number.api.ErrorResponseModel.ErrorMessage;
import ch.sbb.timetable.field.number.entity.Version.Fields;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class AtlasException extends RuntimeException {

  public abstract List<String> getMessageParameters();

  public abstract ExceptionCause getExceptionCause();

  @Getter
  @RequiredArgsConstructor
  public enum ExceptionCause {

    SWISS_NUMBER_CONFLICT(HttpStatus.CONFLICT.value(), Fields.swissTimetableFieldNumber,
        "SwissTimetableFieldNumber {} already taken from {} to {}"),
    NUMBER_CONFLICT(HttpStatus.CONFLICT.value(), Fields.number,
        "Number %s already taken from %s to %s"),

    ;

    private final int httpStatusCode;
    private final String errorField;

    private final String message;

  }

  public ErrorResponseModel toErrorResponse() {

    String message = String.format(
        getExceptionCause().getMessage(),
        getMessageParameters().toArray(new String[0]));
    String errorCode = "LIDI.TTFN." + getExceptionCause().name();
    ErrorMessage errorMessage = ErrorMessage.builder()
                                            .message(message)
                                            .errorCode(errorCode)
                                            .messageParameters(getMessageParameters())
                                            .field(getExceptionCause().getErrorField())
                                            .build();
    return ErrorResponseModel.builder()
                             .httpStatusCode(getExceptionCause().getHttpStatusCode())
                             .errorMessages(Collections.singletonList(errorMessage))
                             .build();
  }

}
