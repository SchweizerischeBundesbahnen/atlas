package ch.sbb.atlas.api.servicepoint.sector;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
@Schema(name = "ReadSectorGroupVersion")
public class ReadSectorGroupVersionModel extends SectorGroupVersionModel {

  @Schema(description = "Sector's related to the sector group")
  List<SectorVersionModel> sectorVersions;

}
