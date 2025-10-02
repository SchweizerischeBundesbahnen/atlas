package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.model.IdCheckable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
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
@Schema(name = "CreateSectorGroupVersion")
public class CreateSectorGroupVersionModel extends SectorGroupVersionModel implements IdCheckable {

  @Size(min = 2)
  @NotNull
  @Schema(description = "Sector sloid's related to the sector group")
  private Set<String> sectorSloids;

}
