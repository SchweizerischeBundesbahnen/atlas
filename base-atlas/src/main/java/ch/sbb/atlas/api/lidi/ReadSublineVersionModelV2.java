package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "ReadSublineVersionV2")
public class ReadSublineVersionModelV2 extends CreateSublineVersionModelV2 implements DatesValidator {

  @Schema(description = "MainSwissLineNumber", example = "IC61", accessMode = AccessMode.READ_ONLY)
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String mainSwissLineNumber;

  @Schema(description = "MainLineNumber", example = "IC61", accessMode = AccessMode.READ_ONLY)
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String mainLineNumber;

  @Schema(description = "ShortNumber", example = "61", accessMode = AccessMode.READ_ONLY)
  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String mainShortNumber;

  @Schema(description = "offerCategory", example = "IC", accessMode = AccessMode.READ_ONLY)
  private OfferCategory mainLineOfferCategory;

}
