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
 * Financing and the cableway fields may only be set or changed by users with at least the SUPERVISOR role.
 */
@RequiredArgsConstructor
@Getter
public class LineV3SupervisorOnlyFieldException extends AtlasException {

  private static final String ERROR_CODE = "LIDI.LINE.ERROR.SUPERVISOR_ONLY_FIELD";
  private static final String ERROR = "Field may only be changed by a supervisor";

  private final Set<String> fields;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("You are not permitted to set or change the following fields: " + new TreeSet<>(fields))
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return fields.stream()
        .map(field -> Detail.builder()
            .field(field)
            .message("Field {0} may only be set or changed by users with at least the SUPERVISOR role")
            .displayInfo(builder()
                .code(ERROR_CODE)
                .with("field", field)
                .build())
            .build())
        .collect(Collectors.toSet());
  }

}
