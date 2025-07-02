package ch.sbb.atlas.api.servicepoint.sector.relation;

import ch.sbb.atlas.api.AtlasFieldLengths;
import jakarta.validation.constraints.NotNull;
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
public class SectorGroupRelationModel {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @NotNull
  private String sectorSloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @NotNull
  private String sectorGroupSloid;

}
