package ch.sbb.atlas.configuration.handler;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.DisplayInfoBuilder;
import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.path.PathImpl;

@RequiredArgsConstructor
public class ConstraintViolationMapper {

  private final Set<ConstraintViolation<?>> constraintViolations;

  public SortedSet<Detail> getDetails() {
    SortedSet<Detail> details = new TreeSet<>();
    constraintViolations.forEach(constraintViolation -> {
      DisplayInfoBuilder displayInfoBuilder = DisplayInfo.builder()
          .code(getErrorCodeForConstraintMessageTemplate(constraintViolation.getMessageTemplate()))
          .with("propertyPath", constraintViolation.getPropertyPath().toString())
          .with("value", constraintViolation.getInvalidValue().toString());
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
    return switch (messageTemplate) {
      case "{jakarta.validation.constraints.Size.message}" -> "ERROR.CONSTRAINT_VIOLATION.SIZE";
      case "{jakarta.validation.constraints.Pattern.message}" -> "ERROR.CONSTRAINT_VIOLATION.PATTERN";
      case "{jakarta.validation.constraints.NotBlank.message}" -> "ERROR.CONSTRAINT_VIOLATION.NOT_BLANK";
      case "{jakarta.validation.constraints.NotNull.message}" -> "ERROR.CONSTRAINT_VIOLATION.NOT_NULL";
      case "StopPointType only allowed for StopPoint" -> "ERROR.CONSTRAINT_VIOLATION.STOP_POINT_TYPE_ONLY_ALLOWED_FOR_STOPPOINT";
      case "FreightServicePoint in CH needs sortCodeOfDestinationStation" -> "ERROR.CONSTRAINT_VIOLATION.FREIGHT_SERVICE_POINT";
      case "At most one of OperatingPointTechnicalTimetableType, OperatingPointTrafficPointType may be set" -> "ERROR"
          + ".CONSTRAINT_VIOLATION.SERVICE_POINT_TYPE";
      default -> "ERROR.CONSTRAINT_VIOLATION.DEFAULT";
    };
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
