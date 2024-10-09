package ch.sbb.atlas.versioning.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor
@Getter
public class DateValidationException extends AtlasException {

  private final String message;


  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(message)
            .error(message)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
            .message(message)
            .displayInfo(builder()
                    .code("VALIDATION.DATE_RANGE_ERROR")
                    .build())
            .build());
  }
}
