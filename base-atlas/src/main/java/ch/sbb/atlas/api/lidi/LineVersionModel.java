package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
@Schema(name = "LineVersion")
public class LineVersionModel extends BaseVersionModel implements DatesValidator {

  private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6})$";
  private static final String CMYK_COLOR_PATTERN = "^(([0-9][0-9]?|100),){3}([0-9][0-9]?|100)$";

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @NotBlank
  @Size(min = 1, max = 50)
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String swissLineNumber;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "LineType")
  @NotNull
  private LineType lineType;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001234")
  private String slnid;

  @Schema(description = "PaymentType")
  @NotNull
  private PaymentType paymentType;

  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

  @Schema(description = "AlternativeName", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String alternativeName;

  @Schema(description = "CombinationName", example = "S L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String combinationName;

  @Schema(description = "LongName", example = "Spiseggfräser; Talstation - Bergstation; Ersatzbus")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String longName;

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

  @Schema(description = "Icon", example = "https://commons.wikimedia.org/wiki/File:Metro_de_Bilbao_L1.svg")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String icon;

  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation SBOID", example = "ch:1:sboid:100001")
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String businessOrganisation;

  @Schema(description = "Comment", example = "Comment regarding the line")
  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String comment;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

  @Schema(description = "Workflows related to the line version", accessMode = AccessMode.READ_ONLY)
  private Set<LineVersionWorkflowModel> lineVersionWorkflows;
}
