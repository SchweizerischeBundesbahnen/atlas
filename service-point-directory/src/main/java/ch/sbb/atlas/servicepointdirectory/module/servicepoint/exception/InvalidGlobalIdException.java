package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class InvalidGlobalIdException extends AtlasException {

  private static final String FIELD = "globalId";
  private static final String CODE_PREFIX = "SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.";

  private final transient HttpStatus httpStatus;
  private final String message;
  private final String code;
  private final transient List<Parameter> parameters;

  private InvalidGlobalIdException(HttpStatus httpStatus, String message, String code, List<Parameter> parameters) {
    this.httpStatus = httpStatus;
    this.message = message;
    this.code = code;
    this.parameters = parameters;
  }

  public static InvalidGlobalIdException countryMismatch(String expectedPrefix, String globalId) {
    return new InvalidGlobalIdException(HttpStatus.BAD_REQUEST,
        "Global-ID '" + globalId + "' must start with '" + expectedPrefix + "' for this country.",
        CODE_PREFIX + "COUNTRY_MISMATCH",
        List.of(new Parameter("prefix", expectedPrefix)));
  }

  public static InvalidGlobalIdException notAllowedForCountry() {
    return new InvalidGlobalIdException(HttpStatus.BAD_REQUEST,
        "A Global-ID can only be entered for German (11, 80) or Austrian (12, 81) stopPoints.",
        CODE_PREFIX + "NOT_ALLOWED_FOR_COUNTRY", List.of());
  }

  public static InvalidGlobalIdException alreadyUsed(GlobalId globalId) {
    return new InvalidGlobalIdException(HttpStatus.CONFLICT,
        "Global-ID '" + globalId.value() + "' is already used by another stopPoint.",
        CODE_PREFIX + "ALREADY_USED", List.of(new Parameter(FIELD, globalId.value())));
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(httpStatus.value())
        .message(message)
        .error(message)
        .details(new TreeSet<>(List.of(Detail.builder()
            .field(FIELD)
            .message(message)
            .displayInfo(DisplayInfo.builder()
                .code(code)
                .with(parameters)
                .build())
            .build())))
        .build();
  }
}
