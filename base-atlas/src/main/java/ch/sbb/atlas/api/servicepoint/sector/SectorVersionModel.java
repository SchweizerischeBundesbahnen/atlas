package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
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
@Schema(name = "SectorVersion")
public class SectorVersionModel extends BaseSectorModel {

  @NotNull
  @Digits(integer = 8, fraction = 11)
  @Schema(description = "North longitude", example = "225738.00000000000")
  private Double north;

  @NotNull
  @Digits(integer = 8, fraction = 11)
  @Schema(description = "Eastern longitude", example = "681821.00000000000")
  private Double east;

  @Schema(description = "Height of the coordinate point", example = "540.20000")
  @Digits(integer = 5, fraction = 4)
  private Double height;

  @Schema(description = "Coordinate system spatial reference", example = "LV95")
  @NotNull
  private SpatialReference spatialReference;

  @Schema(description = "Height of edge", example = "18")
  @Digits(integer = 3, fraction = 0)
  @Min(0)
  private Double edgeHeight;

}
