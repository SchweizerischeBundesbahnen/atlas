package ch.sbb.line.directory.api;

import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "LineVersion")
public class LineVersionModel {

  private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6})$";
  private static final String CMYK_COLOR_PATTERN = "^(([0-9][0-9]?|100),){3}([0-9][0-9]?|100)$";

  @Schema(description = "Technical identifier")
  private Long id;

  @Schema(description = "SwissLineNumber")
  private String swissLineNumber;

  @Schema(description = "Status")
  private Status status;

  @Schema(description = "LineType")
  private LineType type;

  @Schema(description = "SLNID")
  private String slnid;

  @Schema(description = "PaymentType")
  private PaymentType paymentType;

  @Schema(description = "ShortName")
  private String shortName;

  @Schema(description = "AlternativeName")
  private String alternativeName;

  @Schema(description = "CombinationName")
  private String combinationName;

  @Schema(description = "LongName")
  private String longName;

  @Pattern(regexp = HEX_COLOR_PATTERN)
  @Schema(description = "Color of the font in RGB")
  private String colorFontRgb;

  @Pattern(regexp = HEX_COLOR_PATTERN)
  @Schema(description = "Color of the background in RGB")
  private String colorBackRgb;

  @Pattern(regexp = CMYK_COLOR_PATTERN)
  @Schema(description = "Color of the font in CMYK")
  private String colorFontCmyk;

  @Pattern(regexp = CMYK_COLOR_PATTERN)
  @Schema(description = "Color of the background in CMYK")
  private String colorBackCmyk;

  @Schema(description = "Icon")
  private String icon;

  @Schema(description = "Description")
  private String description;

  @Schema(description = "Valid from")
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation")
  private String businessOrganisation;

  @Schema(description = "Comment")
  private String comment;

}
