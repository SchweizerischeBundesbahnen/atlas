package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Raised when a bulk import line carries both a sloid and a service point number, but they do not describe the same service
 * point. Silently preferring one of the two would apply the line to a stop the file did not mean.
 */
@Getter
public final class ServicePointIdentifierMismatchException extends AtlasException {

  private static final String FIELD = "sloid";

  private final transient String sloid;
  private final transient Integer number;

  public ServicePointIdentifierMismatchException(String sloid, Integer number) {
    this.sloid = sloid;
    this.number = number;
  }

  @Override
  public ErrorResponse getErrorResponse() {
    String message = "Sloid " + sloid + " does not belong to service point number " + number + ".";
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(message)
        .error(message)
        .details(new TreeSet<>(List.of(Detail.builder()
            .field(FIELD)
            .message(message)
            .displayInfo(DisplayInfo.builder()
                .code("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.SLOID_NUMBER_MISMATCH")
                .with(FIELD, sloid)
                .with("number", String.valueOf(number))
                .build())
            .build())))
        .build();
  }
}
