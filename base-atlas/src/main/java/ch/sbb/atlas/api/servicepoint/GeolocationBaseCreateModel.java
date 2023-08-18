package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import io.swagger.v3.oas.annotations.media.Schema;
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
  private Double height;

}
