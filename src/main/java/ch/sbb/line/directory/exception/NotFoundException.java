package ch.sbb.line.directory.exception;

import static ch.sbb.line.directory.api.ErrorResponse.DisplayInfo.builder;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class NotFoundException extends AtlasException {

  private static final String CODE = "ERROR.ENTITY_NOT_FOUND";

  private final String field;
  private final String value;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .httpStatus(HttpStatus.NOT_FOUND.value())
                        .message("Entity not found")
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
                          .field(field)
                          .message("Object with {0} {1} not found")
                          .displayInfo(builder()
                              .code(CODE)
                              .with("field", field)
                              .with("value", value)
                              .build())
                          .build();
    return List.of(detail);
  }
}
