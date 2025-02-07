package ch.sbb.atlas.configuration.handler;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.DisplayInfoBuilder;
import jakarta.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.path.PathImpl;

@RequiredArgsConstructor
public class ConstraintViolationMapper {

  private static final class ErrorInfo {

    private final String code;
    private final Function<ConstraintViolation<?>, String> message;

    ErrorInfo(String code) {
      this(code, ConstraintViolation::getMessage);
    }

    ErrorInfo(String code, Function<ConstraintViolation<?>, String> message) {
      this.code = code;
      this.message = message;
    }
  }

  private static final ErrorInfo defaultErrorInfo = new ErrorInfo("ERROR.CONSTRAINT_VIOLATION.DEFAULT");
  private static final Map<String, ErrorInfo> ERROR_CODE_MAP = new HashMap<>();

  static {
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.Size.message}", new ErrorInfo("ERROR.CONSTRAINT_VIOLATION.SIZE"));
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.Pattern.message}", new ErrorInfo("ERROR.CONSTRAINT_VIOLATION.PATTERN"));
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.NotBlank.message}",
        new ErrorInfo("ERROR.CONSTRAINT_VIOLATION.NOT_BLANK"));
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.NotNull.message}", new ErrorInfo("ERROR.CONSTRAINT_VIOLATION.NOT_NULL"));
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.Digits.message}", new ErrorInfo("ERROR.CONSTRAINT_VIOLATION.DIGITS"));
    ERROR_CODE_MAP.put("{atlas.constraint.validSpatialReferenceFraction}", new ErrorInfo(
        "ERROR.CONSTRAINT_VIOLATION.VALID_SPATIAL_REFERENCE_FRACTION",
        cv -> "Max decimal places exceeded. LV03 and LV95 max. 5. WGS84 and WGS84WEB max. 11."));
    ERROR_CODE_MAP.put("{atlas.constraint.validServicePointNumber}", new ErrorInfo(
        "ERROR.CONSTRAINT_VIOLATION.VALID_SERVICE_POINT_NUMBER",
        cv -> "numberShort must be present only if country not in (85,11,12,13,14)"));
  }

  private static String propertyName(ConstraintViolation<?> cv) {
    return ((PathImpl) cv.getPropertyPath()).getLeafNode().getName();
  }

  private static ErrorInfo errorInfo(ConstraintViolation<?> cv) {
    return ERROR_CODE_MAP.getOrDefault(cv.getMessageTemplate(), defaultErrorInfo);
  }

  private final Set<ConstraintViolation<?>> constraintViolations;

  public SortedSet<Detail> getDetails() {
    SortedSet<Detail> details = new TreeSet<>();
    constraintViolations.forEach(constraintViolation -> {
      DisplayInfoBuilder displayInfoBuilder = DisplayInfo.builder()
          .code(errorInfo(constraintViolation).code)
          .with("propertyPath", propertyName(constraintViolation))
          .with("value", String.valueOf(constraintViolation.getInvalidValue()));
      constraintViolation.getConstraintDescriptor().getAttributes()
          .forEach((key, value) -> {
            if (!value.toString().contains("java.lang.Class")) {
              displayInfoBuilder.with(key, value.toString());
            }
          });

      details.add(Detail.builder()
          .message(errorInfo(constraintViolation).message.apply(constraintViolation))
          .field(propertyName(constraintViolation))
          .displayInfo(displayInfoBuilder.build())
          .build());
    });

    return details;
  }

  public String getMessage() {
    final Function<ConstraintViolation<?>, String> cvToString = cv -> String.format(
        "Path parameter '%s' value '%s' %s",
        propertyName(cv),
        cv.getInvalidValue(),
        errorInfo(cv).message.apply(cv)
    );
    return "Constraint for Path parameter was violated: " + new HashSet<>(constraintViolations.stream().map(cvToString).toList());
  }

}
