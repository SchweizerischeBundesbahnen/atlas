package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "UpdateServicePointVersion")
public class UpdateServicePointVersionModel extends ServicePointVersionModel {

  @Schema(description = "Five digits number. Represent service point ID.", example = "34505")
  @Min(AtlasFieldLengths.MIN_NUMBER)
  @Max(AtlasFieldLengths.MAX_FIVE_DIGITS_NUMBER)
  private Integer numberShort;

  @Schema(description = "The country for the service point. Only needed if ServicePointNumber is created automatically",
      example = "SWITZERLAND")
  @NotNull
  private Country country;

  @Min(value = AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER, message = "Minimum value for number.")
  @Max(value = AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER, message = "Maximum value for number.")
  @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
      + "operatingPointRouteNetwork", example = "8034505")
  private Integer operatingPointKilometerMasterNumber;

  @Valid
  private GeolocationBaseCreateModel servicePointGeolocation;

  @JsonIgnore
  public boolean isOperatingPoint() {
    return !isRawServicePoint();
  }

  @JsonIgnore
  public boolean isPureOperatingPoint() {
    return getOperatingPointTechnicalTimetableType() != null;
  }

  @JsonIgnore
  public boolean isOperatingPointWithTimetable() {
    return isOperatingPoint() && getOperatingPointType() == null;
  }

  @JsonIgnore
  @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
  public boolean isValidFreightServicePoint() {
    if (numberShort == null) {
      return true;
    }
    return !(getCountry() == Country.SWITZERLAND && isFreightServicePoint() && !getValidFrom().isBefore(
        LocalDate.now()))
        || StringUtils.isNotBlank(super.getSortCodeOfDestinationStation());
  }

  @JsonIgnore
  public boolean isStopPoint() {
    return !getMeansOfTransport().isEmpty();
  }

  @JsonIgnore
  @AssertTrue(message = "StopPointType only allowed for StopPoint")
  boolean isValidStopPointWithType() {
    return isStopPoint() || getStopPointType() == null;
  }

  @JsonIgnore
  public boolean shouldGenerateServicePointNumber() {
    return ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(country);
  }

  @JsonIgnore
  @AssertTrue(message = "If OperatingPointRouteNetwork is true, then operatingPointKilometerMaster will be set to the same "
          + "value as numberWithoutCheckDigit and it should not be sent in the request")
  public boolean isOperatingPointRouteNetworkTrueAndKilometerMasterNumberNull() {
    return !isOperatingPointRouteNetwork() || operatingPointKilometerMasterNumber == null;
  }

  public Integer setKilometerMasterNumberDependingOnRouteNetworkValue() {
    if (!isOperatingPointRouteNetwork()) {
      return operatingPointKilometerMasterNumber;
    } else if (numberShort != null) {
      return ServicePointNumber.of(country, numberShort).getNumber();
    }
    return null;
  }

  @JsonIgnore
  @AssertTrue(message = "OperatingPointRouteNetwork true is allowed only for StopPoint, ControlPoint and OperatingPoint." +
          " OperatingPointKilometerMasterNumber can be set only for StopPoint, ControlPoint and OperatingPoint.")
  public boolean isRouteNetworkOrKilometerMasterNumberAllowed() {
    return !isOperatingPointRouteNetwork() && operatingPointKilometerMasterNumber == null || (isStopPoint() || isPureOperatingPoint() || isFreightServicePoint());
  }

}
