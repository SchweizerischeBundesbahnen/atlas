package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class LineVersionModelV2 extends BaseLineVersionModel {

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @Size(min = 1, max = 50)
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String swissLineNumber;

  @Schema(description = "LineType")
  @NotNull
  private LineType lineType;

  @Schema(description = "ConcessionType")
  private LineConcessionType lineConcessionType;

  @Schema(description = "ShortNumber", example = "61")
  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String shortNumber;

  @Schema(description = "offerCategory")
  @NotNull
  private OfferCategory offerCategory;
}
