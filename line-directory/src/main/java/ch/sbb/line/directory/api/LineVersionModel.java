package ch.sbb.line.directory.api;

import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "LineVersion")
public class LineVersionModel implements DatesValidator {

  private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6})$";
  private static final String CMYK_COLOR_PATTERN = "^(([0-9][0-9]?|100),){3}([0-9][0-9]?|100)$";

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY)
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
  private LineType type;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001234")
  private String slnid;

  @Schema(description = "PaymentType")
  @NotNull
  private PaymentType paymentType;

  @Schema(description = "Number", example = "L1")
  @Size(max = 50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

  @Schema(description = "AlternativeName", example = "L1")
  @Size(max = 50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String alternativeName;

  @Schema(description = "CombinationName", example = "S L1")
  @Size(max = 50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String combinationName;

  @Schema(description = "LongName", example = "Spiseggfräser; Talstation - Bergstation; Ersatzbus")
  @Size(max = 255)
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
  @Size(max = 255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String icon;

  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(max = 255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation", example = "11 - SBB - Schweizerische Bundesbahnen - 100001")
  @NotBlank
  @Size(min = 1, max = 50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String businessOrganisation;

  @Schema(description = "Comment", example = "Comment regarding the line")
  @Size(max = 1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String comment;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;
}
