package ch.sbb.atlas.api.location;

import ch.sbb.atlas.servicepoint.SloidValidation;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimSloidRequestModel(@NotNull(message = "SloidType must not be null") SloidType sloidType,
                                     @NotBlank(message = "sloid must not be blank") String sloid) {

  @AssertTrue(message = "Sloid not valid for provided sloidType")
  public boolean isValidSloid() {
    if (sloidType == null || sloid == null) {
      return false;
    }
    return switch (sloidType) {
      case SERVICE_POINT -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_SERVICE_POINT);
      case AREA, TOILET, REFERENCE_POINT, PARKING_LOT, INFO_DESK, TICKET_COUNTER, RELATION ->
          SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_AREA);
      case PLATFORM -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_PLATFORM);
    };
  }

}
