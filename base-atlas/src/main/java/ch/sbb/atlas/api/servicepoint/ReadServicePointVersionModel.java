package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "ReadServicePointVersion")
public class ReadServicePointVersionModel extends ServicePointVersionModel {

  @NotNull
  @Valid
  private ServicePointNumber number;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String sloid;

  @Valid
  @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
      + "operatingPointRouteNetwork")
  private ServicePointNumber operatingPointKilometerMaster;

  @NotNull
  @Schema(description = "Status", example = "VALIDATED")
  private Status status;

  @Schema(description = "Indicates if this a operatingPoint.")
  private boolean operatingPoint;

  @Schema(description = "Indicates if this a operatingPoint including Timetables.")
  private boolean operatingPointWithTimetable;

  private ServicePointGeolocationReadModel servicePointGeolocation;

  @JsonInclude
  @NotNull
  public Country getCountry() {
    return number.getCountry();
  }

  @JsonInclude
  @Schema(description = "ServicePoint has a Geolocation")
  public boolean isHasGeolocation() {
    return servicePointGeolocation != null;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is OperatingPointKilometer")
  public boolean isOperatingPointKilometer() {
    return operatingPointKilometerMaster != null;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is StopPoint")
  public boolean isStopPoint() {
    return !getMeansOfTransport().isEmpty();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is FareStop", example = "false")
  public boolean isFareStop() {
    return getOperatingPointTrafficPointType() == OperatingPointTrafficPointType.TARIFF_POINT;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is TrafficPoint")
  public boolean isTrafficPoint() {
    return isStopPoint() || isFreightServicePoint() || isFareStop();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is BorderPoint", example = "false")
  public boolean isBorderPoint() {
    return getOperatingPointTechnicalTimetableType() == OperatingPointTechnicalTimetableType.COUNTRY_BORDER;
  }

}
