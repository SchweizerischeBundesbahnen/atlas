package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Schema(name = "LineVersionV2")
public class LineVersionModelV2 extends BaseLineVersionModel  {

  @Schema(description = "ConcessionType")
  @NotNull
  private LineConcessionType lineConcessionType;

  @Schema(description = "ShortNumber", example = "61")
  @Size(max = AtlasFieldLengths.LENGTH_50)//todo: ask Judith
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  private String shortNumber;

  @Schema(description = "offerCategory", example = "IC")
  @Size(max = AtlasFieldLengths.LENGTH_50)//todo: ask Judith
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  private String offerCategory;
}
