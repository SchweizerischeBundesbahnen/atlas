package ch.sbb.line.directory.api;

import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CymkColor;
import ch.sbb.line.directory.model.RgbColor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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

  @Schema(description = "Technical identifier")
  private Long id;

  @Schema(description = "SwissLineNumber")
  private String swissLineNumber;

  @Builder.Default
  @Schema(description = "Subline Versions")
  private Set<SublineVersionModel> sublineVersions = new HashSet<>();

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

  @Schema(description = "Color of the font in RGB")
  private RgbColor colorFontRgb;

  @Schema(description = "Color of the background in RGB")
  private RgbColor colorBackRgb;

  @Schema(description = "Color of the font in CMYK")
  private CymkColor colorFontCmyk;

  @Schema(description = "Color of the background in CMYK")
  private CymkColor colorBackCmyk;

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
