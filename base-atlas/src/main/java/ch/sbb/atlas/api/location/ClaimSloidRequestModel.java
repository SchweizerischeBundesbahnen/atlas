package ch.sbb.atlas.api.location;

import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.SloidValidation;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimSloidRequestModel(
    @NotNull SloidType sloidType,
    @NotBlank String sloid,
    Country country) {

  @AssertTrue(message = "When SloidType = SERVICE_POINT, the country has to be one of code: 85 or 11-14")
  public boolean isValidCountry() {
    return sloidType != SloidType.SERVICE_POINT || (country != null && ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(
        country));
  }

  @AssertTrue(message = "Sloid not valid for provided sloidType")
  public boolean isValidSloid() {
    return switch (sloidType) {
      case SERVICE_POINT -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_SERVICE_POINT);
      case AREA -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_AREA);
      case PLATFORM -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_PLATFORM);
    };
  }

}
