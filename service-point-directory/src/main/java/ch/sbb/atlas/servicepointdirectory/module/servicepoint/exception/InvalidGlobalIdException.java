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

/**
 * Raised when an entered Global-ID does not satisfy the business rules (wrong country prefix, not
 * allowed for the given country, leading/trailing whitespace, too long or already used on another
 * stop). Carries a display code so the frontend can render a comprehensible message.
 */
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
        "A Global-ID can only be entered for German (11, 80) or Austrian (12, 81) stops.",
        CODE_PREFIX + "NOT_ALLOWED_FOR_COUNTRY", List.of());
  }

  public static InvalidGlobalIdException whitespace() {
    return new InvalidGlobalIdException(HttpStatus.BAD_REQUEST,
        "Global-ID must not contain leading or trailing whitespace.",
        CODE_PREFIX + "WHITESPACE", List.of());
  }

  public static InvalidGlobalIdException maxLength(int maxLength) {
    return new InvalidGlobalIdException(HttpStatus.BAD_REQUEST,
        "Global-ID must not exceed " + maxLength + " characters.",
        CODE_PREFIX + "MAX_LENGTH", List.of(new Parameter("maxLength", String.valueOf(maxLength))));
  }

  public static InvalidGlobalIdException alreadyUsed(GlobalId globalId) {
    return new InvalidGlobalIdException(HttpStatus.CONFLICT,
        "Global-ID '" + globalId.value() + "' is already used by another stop.",
        CODE_PREFIX + "ALREADY_USED", List.of(new Parameter(FIELD, globalId.value())));
  }

  public static InvalidGlobalIdException illegalArguments() {
    return new InvalidGlobalIdException(HttpStatus.BAD_REQUEST,
        "Global-ID must have non-null value.",
        CODE_PREFIX + "ILLEGAL_ARGUMENT", List.of());
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
