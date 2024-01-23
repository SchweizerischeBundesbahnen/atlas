package ch.sbb.atlas.api.location;

import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.SloidValidation;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ClaimSloidRequestModel{

  @NotNull
  private SloidType sloidType;
  @NotBlank
  private String sloid;

  private Country country;

  public ClaimSloidRequestModel(@NotNull SloidType sloidType, String sloid, Country country) {
    this.sloidType = sloidType;
    this.sloid = sloid;
    this.country = country;
  }
  public ClaimSloidRequestModel(@NotNull SloidType sloidType, String sloid) {
    this.sloidType = sloidType;
    this.sloid = sloid;
  }

  public ClaimSloidRequestModel(String sloid) {
    this.sloid = sloid;
  }

  @AssertTrue(message = "When SloidType = SERVICE_POINT, the country has to be one of code: 85 or 11-14")
  public boolean isValidCountry() {
    return sloidType != SloidType.SERVICE_POINT || (country != null && ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(
        country));
  }

  @AssertTrue(message = "Sloid not valid for provided sloidType")
  public boolean isValidSloid() {
    return switch (sloidType) {
      case SERVICE_POINT -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_SERVICE_POINT);
      case AREA, TOILETTE, REFERENCE_POINT, RELATION, PARKING_LOT, INFO_DESK, TICKET_COUNTER -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_AREA);
      case PLATFORM -> SloidValidation.isSloidValid(sloid, SloidValidation.EXPECTED_COLONS_PLATFORM);
    };
  }

}
