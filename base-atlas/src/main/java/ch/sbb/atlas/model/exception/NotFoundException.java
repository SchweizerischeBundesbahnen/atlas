package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public abstract class NotFoundException extends AtlasException {

  private static final String CODE = "ERROR.ENTITY_NOT_FOUND";
  private static final String ERROR = "Not found";

  private final String field;
  private final String value;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message("Entity not found")
        .error(ERROR)
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
        .field(field)
        .message("Object with {0} {1} not found")
        .displayInfo(ErrorResponse.DisplayInfo.builder()
            .code(CODE)
            .with("field", field)
            .with("value", value)
            .build())
        .build();
    SortedSet<Detail> details = new TreeSet<>();
    details.add(detail);
    return details;
  }

  public static class IdNotFoundException extends NotFoundException {

    private static final String ID = "id";

    public IdNotFoundException(Long value) {
      super(ID, String.valueOf(value));
    }
  }

}
