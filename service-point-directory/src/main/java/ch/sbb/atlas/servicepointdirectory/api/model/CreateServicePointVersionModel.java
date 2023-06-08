package ch.sbb.atlas.servicepointdirectory.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

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
    @Min(1000000)
    @Max(9999999)
    private Integer numberWithoutCheckDigit;

    @Min(value = 1000000, message = "Minimum value for number.")
    @Max(value = 9999999, message = "Maximum value for number.")
    @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
        + "operatingPointRouteNetwork")
    private Integer operatingPointKilometerMasterNumber;

    @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
    public boolean isValidFreightServicePoint() {
        return !(super.isFreightServicePoint() && !getValidFrom().isBefore(LocalDate.now()))
            || StringUtils.isNotBlank(super.getSortCodeOfDestinationStation());
    }

}
