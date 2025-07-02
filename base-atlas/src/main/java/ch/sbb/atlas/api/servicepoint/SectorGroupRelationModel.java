package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@Builder
@Schema(name = "SectorGroupRelationModel")
public class SectorGroupRelationModel {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "")
  private String sectorSloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "")
  private String sectorGroupSloid;

}
