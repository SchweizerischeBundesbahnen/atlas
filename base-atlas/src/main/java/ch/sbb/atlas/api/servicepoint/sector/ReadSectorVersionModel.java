package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(name = "ReadSectorVersion")
public class ReadSectorVersionModel extends SectorVersionModel {

  @Schema(description = "Unique code for sector that is used in customer information.\n" +
      "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:16161:1")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @NotNull
  private String sloid;

}
