package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateServicePointVersion")
public class CreateServicePointVersionModel extends ServicePointVersionModel {

  @Schema(description = "Seven digits number. First two digits represent Country Code. "
          + "Last 5 digits represent service point ID.", example = "8034505")
  @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
  @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER)
  private Integer numberWithoutCheckDigit;

  @Min(value = AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER, message = "Minimum value for number.")
  @Max(value = AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER, message = "Maximum value for number.")
  @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
          + "operatingPointRouteNetwork", example = "8034505")
  private Integer operatingPointKilometerMasterNumber;

  @JsonIgnore
  @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
  public boolean isValidFreightServicePoint() {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(numberWithoutCheckDigit);
    return !(servicePointNumber.getCountry() == Country.SWITZERLAND && super.isFreightServicePoint() && !getValidFrom().isBefore(
            LocalDate.now()))
            || StringUtils.isNotBlank(super.getSortCodeOfDestinationStation());
  }

}