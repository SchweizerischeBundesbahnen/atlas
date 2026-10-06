package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Size;
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
@Schema(name = "ReferencedLineV3")
public class ReferencedLineModelV3 {

  @Schema(description = "Number of the referenced line", accessMode = AccessMode.READ_ONLY, example = "90.583")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String number;

  @Schema(description = "LineNumber of the referenced line", accessMode = AccessMode.READ_ONLY, example = "90.583")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String lineNumber;

  @Schema(description = "SwissLineNumber of the referenced line", accessMode = AccessMode.READ_ONLY, example = "90.583")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String swissLineNumber;

  @Schema(description = "SLNID of the referenced line", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:1028629")
  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String lineSlnid;

  @Schema(description = "ConcessionType of the referenced line", accessMode = AccessMode.READ_ONLY)
  private ConcessionType concessionType;

}
