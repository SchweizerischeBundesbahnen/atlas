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
 * Lines (versions without a referenced line) must provide number and offerCategory, analogous to the V2 write contract.
 */
@RequiredArgsConstructor
@Getter
public class LineV3MandatoryFieldException extends AtlasException {

  private static final String ERROR_CODE = "LIDI.LINE.ERROR.MANDATORY.FIELD";
  private static final String ERROR = "Mandatory field missing on line";

  private final Set<String> fields;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Lines must provide the mandatory fields: " + new TreeSet<>(fields))
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return fields.stream()
        .map(field -> Detail.builder()
            .field(field)
            .message("Field {0} is mandatory for a line")
            .displayInfo(builder()
                .code(ERROR_CODE)
                .with("field", field)
                .build())
            .build())
        .collect(Collectors.toSet());
  }

}
