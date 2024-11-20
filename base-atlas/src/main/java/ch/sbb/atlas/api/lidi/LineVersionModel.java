package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * @deprecated since V2.328.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "LineVersion", description = "Deprecated in favor of LineVersionV2")
@Deprecated(forRemoval = true, since = "2.328.0")
public class LineVersionModel extends BaseLineVersionModel {

  private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6})$";
  private static final String CMYK_COLOR_PATTERN = "^(([0-9][0-9]?|100),){3}([0-9][0-9]?|100)$";

  @Schema(description = "LineType")
  @NotNull
  private LineType lineType;

  @Schema(description = "PaymentType deprecated since V2.328.0", accessMode = AccessMode.READ_ONLY)
  private PaymentType paymentType;

  @Schema(description = "AlternativeName", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String alternativeName;

  @Schema(description = "CombinationName", example = "S L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String combinationName;

  @Schema(description = "Icon", example = "https://commons.wikimedia.org/wiki/File:Metro_de_Bilbao_L1.svg")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String icon;

  @Schema(description = "Color of the font in RGB", example = "#FF0000")
  @Pattern(regexp = HEX_COLOR_PATTERN)
  @NotNull
  private String colorFontRgb;

  @Schema(description = "Color of the background in RGB", example = "#FF0000")
  @Pattern(regexp = HEX_COLOR_PATTERN)
  @NotNull
  private String colorBackRgb;

  @Schema(description = "Color of the font in CMYK", example = "10,100,0,50")
  @Pattern(regexp = CMYK_COLOR_PATTERN)
  @NotNull
  private String colorFontCmyk;

  @Schema(description = "Color of the background in CMYK", example = "10,100,0,50")
  @Pattern(regexp = CMYK_COLOR_PATTERN)
  @NotNull
  private String colorBackCmyk;

}
