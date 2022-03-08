package ch.sbb.line.directory.exception;

import static ch.sbb.line.directory.api.ErrorResponse.DisplayInfo.builder;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import java.util.List;
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

  public static class IdNotFoundException extends NotFoundException {

    public IdNotFoundException(Long value) {
      super(Fields.id, String.valueOf(value));
    }
  }

  public static class SlnidNotFoundException extends NotFoundException {

    public SlnidNotFoundException(String value) {
      super(Fields.slnid, value);
    }
  }

  public static class TtfnidNotFoundException extends NotFoundException {

    public TtfnidNotFoundException(String value) {
      super(TimetableFieldNumberVersion.Fields.ttfnid, value);
    }
  }
}
