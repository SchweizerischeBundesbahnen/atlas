package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
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
@Schema(name = "UpdateLineVersionV2")
public class UpdateLineVersionModelV2 extends BaseLineVersionModel {

  @NotBlank
  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @NotBlank
  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_8)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String swissLineNumber;

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
