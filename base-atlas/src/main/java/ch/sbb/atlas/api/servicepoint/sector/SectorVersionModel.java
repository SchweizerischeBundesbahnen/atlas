package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.model.IdCheckable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
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
public abstract class SectorVersionModel extends BaseSectorModel implements IdCheckable {

  @Schema(description = "Height of edge in cm", example = "180")
  @Digits(integer = 3, fraction = 0)
  private Double edgeHeight;

}
