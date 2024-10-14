package ch.sbb.atlas.versioning.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor
@Getter
public class DateValidationException extends AtlasException {

  private final String message;
  private final LocalDate date;
  private final ValidationType validationType;

  public enum ValidationType {
    MIN,
    MAX
  }


  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(message + date)
            .error(message + date)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
  }

  private List<Detail> getErrorDetails() {
    String code;

    if (validationType == ValidationType.MIN) {
      code = "VALIDATION.MATDATEPICKERMIN";
    } else {
      code = "VALIDATION.MATDATEPICKERMAX";
    }

    return List.of(Detail.builder()
            .message(message)
            .displayInfo(builder()
                    .code(code)
                    .with("date", date)
                    .build())
            .build());
  }
}
