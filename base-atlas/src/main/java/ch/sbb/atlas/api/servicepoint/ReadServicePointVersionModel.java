package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

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

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Status didok3 information")
  private CodeAndDesignation statusDidok3Information;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Details to the categories.")
  private List<CodeAndDesignation> categoriesInformation;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Details to the operationPointType.")
  private CodeAndDesignation operatingPointTypeInformation;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Details to the OperatingPointTechnicalTimetableType.")
  private CodeAndDesignation operatingPointTechnicalTimetableTypeInformation;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private CodeAndDesignation operatingPointTrafficPointTypeInformation;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private List<CodeAndDesignation> meansOfTransportInformation;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Details to the StopPointType.")
  private CodeAndDesignation stopPointTypeInformation;

  @Valid
  @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
          + "operatingPointRouteNetwork")
  private ServicePointNumber operatingPointKilometerMaster;

  @JsonInclude
  @Schema(description = "ServicePoint is OperatingPointKilometer")
  public boolean isOperatingPointKilometer() {
      return operatingPointKilometerMaster != null;
  }

  @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
  public boolean isValidFreightServicePoint() {
    return !(getNumber().getCountry() == Country.SWITZERLAND && super.isFreightServicePoint() && !getValidFrom().isBefore(
            LocalDate.now()))
            || StringUtils.isNotBlank(super.getSortCodeOfDestinationStation());
  }

}
