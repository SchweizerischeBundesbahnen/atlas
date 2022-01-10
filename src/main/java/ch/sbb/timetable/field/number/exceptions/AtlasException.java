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
public class AtlasException extends RuntimeException {

  private final ExceptionCause exceptionCause;
  private final List<String> messageParameters;

  @Getter
  @RequiredArgsConstructor
  public enum ExceptionCause {
    CONFLICT(HttpStatus.CONFLICT.value(), Fields.number, "Number already taken from {} to {}"),

    ;

    private final int httpStatusCode;

    private final String errorField;
    private final String message;

  }

  public ErrorResponseModel toErrorResponse() {
    return ErrorResponseModel.builder()
                             .httpStatusCode(exceptionCause.getHttpStatusCode())
                             .errorMessages(Collections.singletonList(ErrorMessage.builder()
                                                                                  .message(
                                                                                      MessageFormat.format(
                                                                                          exceptionCause.getMessage(),
                                                                                          messageParameters))
                                                                                  .errorCode(
                                                                                      "LIDI.TTFN."
                                                                                          + exceptionCause.name())
                                                                                  .messageParameters(messageParameters)
                                                                                  .build()))
                             .build();
  }

}
