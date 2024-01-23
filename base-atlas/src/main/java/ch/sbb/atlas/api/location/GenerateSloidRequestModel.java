package ch.sbb.atlas.api.location;

import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class GenerateSloidRequestModel {

  @NotNull
  private SloidType sloidType;

  @Pattern(regexp = "ch:1:sloid:[0-9]+")
  private String sloidPrefix;

  private Country country;

  public GenerateSloidRequestModel(@NotNull SloidType sloidType, String sloidPrefix) {
    this.sloidType = sloidType;
    this.sloidPrefix = sloidPrefix;
  }

  public GenerateSloidRequestModel(@NotNull SloidType sloidType, String sloidPrefix, Country country) {
    this.sloidType = sloidType;
    this.sloidPrefix = sloidPrefix;
    this.country = country;
  }

  public GenerateSloidRequestModel(@NotNull SloidType sloidType, Country country) {
    this.sloidType = sloidType;
    this.country = country;
  }

  @AssertTrue(message = "When SloidType = SERVICE_POINT, the country has to be one of code: 85 or 11-14")
  public boolean isValidCountry() {
    return sloidType != SloidType.SERVICE_POINT || (country != null && ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(
        country));
  }

  @AssertTrue(message = "When SloidType = PLATFORM or AREA, the sloidPrefix must be set")
  public boolean isSloidPrefixSet() {
    if (sloidType == SloidType.PLATFORM || sloidType == SloidType.AREA) {
      return sloidPrefix != null;
    }
    return true;
  }

}
