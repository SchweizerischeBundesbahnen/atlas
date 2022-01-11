package ch.sbb.timetable.field.number.exceptions;

import ch.sbb.timetable.field.number.api.ErrorResponseModel;
import ch.sbb.timetable.field.number.api.ErrorResponseModel.ErrorMessage;
import ch.sbb.timetable.field.number.entity.Version.Fields;
import java.text.MessageFormat;
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
    SWISS_NUMBER_CONFLICT(HttpStatus.CONFLICT.value(), Fields.swissTimetableFieldNumber, "SwissTimetableFieldNumber {} already taken from {} to {}"),
    NUMBER_CONFLICT(HttpStatus.CONFLICT.value(), Fields.number, "Number {} already taken from {} to {}"),


    ;

    private final int httpStatusCode;
    private final String errorField;

    private final String message;

  }

  public ErrorResponseModel toErrorResponse() {
    return ErrorResponseModel.builder()
                             .httpStatusCode(getExceptionCause().getHttpStatusCode())
                             .errorMessages(Collections.singletonList(ErrorMessage.builder()
                                                                                  .message(
                                                                                      MessageFormat.format(
                                                                                          getExceptionCause().getMessage(),
                                                                                          getMessageParameters()))
                                                                                  .errorCode(
                                                                                      "LIDI.TTFN."
                                                                                          + getExceptionCause().name())
                                                                                  .messageParameters(getMessageParameters())
                                                                                  .build()))
                             .build();
  }

}
