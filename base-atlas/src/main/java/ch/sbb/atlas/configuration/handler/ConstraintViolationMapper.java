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
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.path.PathImpl;

@RequiredArgsConstructor
public class ConstraintViolationMapper {

  private static final Map<String, String> ERROR_CODE_MAP = new HashMap<>();

  static {
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.Size.message}", "ERROR.CONSTRAINT_VIOLATION.SIZE");
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.Pattern.message}", "ERROR.CONSTRAINT_VIOLATION.PATTERN");
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.NotBlank.message}", "ERROR.CONSTRAINT_VIOLATION.NOT_BLANK");
    ERROR_CODE_MAP.put("{jakarta.validation.constraints.NotNull.message}", "ERROR.CONSTRAINT_VIOLATION.NOT_NULL");
  }

  private final Set<ConstraintViolation<?>> constraintViolations;

  public SortedSet<Detail> getDetails() {
    SortedSet<Detail> details = new TreeSet<>();
    constraintViolations.forEach(constraintViolation -> {
      DisplayInfoBuilder displayInfoBuilder = DisplayInfo.builder()
          .code(getErrorCodeForConstraintMessageTemplate(constraintViolation.getMessageTemplate()))
          .with("propertyPath", constraintViolation.getPropertyPath().toString())
          .with("value", String.valueOf(constraintViolation.getInvalidValue()));
      constraintViolation.getConstraintDescriptor().getAttributes()
          .forEach((key, value) -> {
            if (!value.toString().contains("java.lang.Class")) {
              displayInfoBuilder.with(key, value.toString());
            }
          });

      details.add(Detail.builder()
          .message(constraintViolation.getMessage())
          .field(constraintViolation.getPropertyPath().toString())
          .displayInfo(displayInfoBuilder.build())
          .build());
    });

    return details;
  }

  private String getErrorCodeForConstraintMessageTemplate(String messageTemplate) {
    return ERROR_CODE_MAP.getOrDefault(messageTemplate, "ERROR.CONSTRAINT_VIOLATION.DEFAULT");
  }

  public String getMessage() {
    Set<String> messages = new HashSet<>(constraintViolations.size());
    messages.addAll(constraintViolations.stream()
        .map(
            constraintViolation -> String.format(
                "Path parameter '%s' value '%s' %s",
                ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().getName(),
                constraintViolation.getInvalidValue(),
                constraintViolation.getMessage())).toList());
    return "Constraint for Path parameter was violated: " + messages;
  }

}
