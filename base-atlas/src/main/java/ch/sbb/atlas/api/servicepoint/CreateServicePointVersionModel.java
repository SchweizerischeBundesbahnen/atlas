package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateServicePointVersion")
public class CreateServicePointVersionModel extends UpdateServicePointVersionModel {

  @Schema(description = "Five digits number. Represent service point ID.", example = "34505")
  @Min(AtlasFieldLengths.MIN_NUMBER)
  @Max(AtlasFieldLengths.MAX_FIVE_DIGITS_NUMBER)
  private Integer numberShort;

  @Schema(description = "The country for the service point. Only needed if ServicePointNumber is created automatically",
      example = "SWITZERLAND")
  @NotNull
  private Country country;

  @JsonIgnore
  public boolean shouldGenerateServicePointNumber() {
    return ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(country);
  }

  @JsonIgnore
  @AssertTrue(message = "ServicePointNumber must be present only if country not in (85,11,12,13,14)")
  public boolean isValidServicePointNumber() {
    if (getNumberShort() == null) {
      return shouldGenerateServicePointNumber();
    } else {
      try {
        return !ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(getCountry());
      } catch (NullPointerException exception) {
        return false;
      }
    }
  }

}
