package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Members (sublines) never carry number, swissLineNumber, offerCategory and concessionType themselves. Those values are read
 * from the referenced line and exposed in the referencedLine object.
 */
@RequiredArgsConstructor
@Getter
public class LineV3MemberFieldNotAllowedException extends AtlasException {

  private static final String ERROR_CODE = "LIDI.SUBLINE.ERROR.FIELD_NOT_ALLOWED";
  private static final String ERROR = "Field not allowed on subline";

  private final Set<String> fields;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Sublines must not provide fields that are read from the referenced line: " + new TreeSet<>(fields))
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return fields.stream()
        .map(field -> Detail.builder()
            .field(field)
            .message("Field {0} must not be set on a subline, it is read from the referenced line")
            .displayInfo(builder()
                .code(ERROR_CODE)
                .with("field", field)
                .build())
            .build())
        .collect(Collectors.toSet());
  }

}
