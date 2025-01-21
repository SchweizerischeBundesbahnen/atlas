package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
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
@Schema(name = "CreateLineVersionV2")
public class CreateLineVersionModelV2 extends BaseLineVersionModel {

  @NotNull
  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @NotNull
  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_8)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

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
