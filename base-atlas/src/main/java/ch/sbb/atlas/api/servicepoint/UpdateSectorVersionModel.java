package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Schema(name = "UpdateSectorVersion")
public class UpdateSectorVersionModel {

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @Schema(description = "Designation used in the customer information systems.", example = "Bezeichnung")
  @Size(max = AtlasFieldLengths.LENGTH_40)
  private String designation;

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

  @Schema(description = "", example = "18.000")
  @Digits(integer = 13, fraction = 3)
  @Min(0)
  private Double length;

  @Schema(description = "", example = "18.000")
  @Digits(integer = 13, fraction = 3)
  @Min(0)
  private Double edgeHeight;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;
}
