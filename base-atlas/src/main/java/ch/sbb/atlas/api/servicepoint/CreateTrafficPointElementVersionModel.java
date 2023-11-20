package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateTrafficPointElementVersion")
public class CreateTrafficPointElementVersionModel extends TrafficPointElementVersionModel {

    @Schema(description = "Seven digits number. First two digits represent Country Code. "
            + "Last 5 digits represent traffic point ID.", example = "8034505")
    @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
    @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER)
    @NotNull
    private Integer numberWithoutCheckDigit;

    @Valid
    private GeolocationBaseCreateModel trafficPointElementGeolocation;

    @JsonInclude
    @Schema(description = "TrafficPointElementVersion has a Geolocation")
    public boolean isHasGeolocation() {
        return trafficPointElementGeolocation != null;
    }

    @JsonIgnore
    @AssertTrue(message = """
        SLOID has to end in SID4PT character, not a :
        """)
    public boolean isSloidNotEndingInColon() {
        if (getSloid() == null) {
            return true;
        }
        return !getSloid().endsWith(":");
    }
}
