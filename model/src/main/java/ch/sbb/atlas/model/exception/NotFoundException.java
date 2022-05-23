package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.model.api.ErrorResponse;
import ch.sbb.atlas.model.api.ErrorResponse.Detail;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NotFoundException extends AtlasException {

  private static final String CODE = "ERROR.ENTITY_NOT_FOUND";
  private static final String ERROR = "Not found";
  private static final int NOT_FOUND_STATUS = 404;

  private final String field;
  private final String value;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .status(NOT_FOUND_STATUS)
                        .message("Entity not found")
                        .error(ERROR)
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
                          .field(field)
                          .message("Object with {0} {1} not found")
                          .displayInfo(ErrorResponse.DisplayInfo.builder()
                              .code(CODE)
                              .with("field", field)
                              .with("value", value)
                              .build())
                          .build();
    return List.of(detail);
  }

  public static class IdNotFoundException extends NotFoundException {
    private static final String ID= "id";
    public IdNotFoundException(Long value) {
      super("id", String.valueOf(value));
    }
  }


}
