package ch.sbb.atlas.api.servicepoint;

import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_LV_MAX_DIGITS;
import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_WGS84_MAX_DIGITS;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.math.DoubleOperations;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "GeolocationCreate")
public class GeolocationBaseCreateModel implements TransformableGeolocation {

  @Schema(description = "Coordinate system spatial reference", example = "LV95")
  @NotNull
  private SpatialReference spatialReference;

  @NotNull
  @Schema(description = "North longitude", example = "225738.00000000000")
  private Double north;

  @NotNull
  @Schema(description = "Eastern longitude", example = "681821.00000000000")
  private Double east;

  @Schema(description = "Height of the coordinate point", example = "540.20000")
  @Digits(integer = 5, fraction = 4)
  private Double height;

  @JsonIgnore
  @AssertTrue(message = "Max decimal places exceeded. LV03 and LV95 max. 5. WGS84 and WGS84WEB max. 11.")
  public boolean isValidSpatialReferenceFraction() {
    if (getSpatialReference() == null || getNorth() == null || getEast() == null) {
      return true;
    }
    return switch (getSpatialReference()) {
      case LV03, LV95 ->
          Math.max(DoubleOperations.getFractions(getNorth()), DoubleOperations.getFractions(getEast())) <= ATLAS_LV_MAX_DIGITS;
      case WGS84, WGS84WEB ->
          Math.max(DoubleOperations.getFractions(getNorth()), DoubleOperations.getFractions(getEast())) <= ATLAS_WGS84_MAX_DIGITS;
    };
  }

}
