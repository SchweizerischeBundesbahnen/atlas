package ch.sbb.atlas.api.line;

import ch.sbb.atlas.api.line.enumaration.LineType;
import ch.sbb.atlas.api.line.enumaration.PaymentType;
import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.BaseVersionModel;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "LineVersionSnapshot")
public class LineVersionSnapshotModel extends BaseVersionModel {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "Workflow Technical identifier", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private Long workflowId;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private WorkflowStatus workflowStatus;

  @Schema(description = "Parent Object identifier", example = "Technical Parent Object identifier", accessMode =
      AccessMode.READ_ONLY)
  @NotNull
  private Long parentObjectId;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "LineType", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private LineType lineType;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001234")
  private String slnid;

  @Schema(description = "PaymentType", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private PaymentType paymentType;

  @Schema(description = "Number", example = "L1", accessMode = AccessMode.READ_ONLY)
  private String number;

  @Schema(description = "AlternativeName", example = "L1", accessMode = AccessMode.READ_ONLY)
  private String alternativeName;

  @Schema(description = "CombinationName", example = "S L1", accessMode = AccessMode.READ_ONLY)
  private String combinationName;

  @Schema(description = "LongName", example = "Spiseggfräser; Talstation - Bergstation; Ersatzbus", accessMode =
      AccessMode.READ_ONLY)
  private String longName;

  @Schema(description = "Color of the font in RGB", example = "#FF0000", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private String colorFontRgb;

  @Schema(description = "Color of the background in RGB", example = "#FF0000", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private String colorBackRgb;

  @Schema(description = "Color of the font in CMYK", example = "10,100,0,50", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private String colorFontCmyk;

  @Schema(description = "Color of the background in CMYK", example = "10,100,0,50", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private String colorBackCmyk;

  @Schema(description = "Icon", example = "https://commons.wikimedia.org/wiki/File:Metro_de_Bilbao_L1.svg", accessMode =
      AccessMode.READ_ONLY)
  private String icon;

  @Schema(description = "Description", example = "Meiringen - Innertkirchen", accessMode = AccessMode.READ_ONLY)
  private String description;

  @Schema(description = "Valid from", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation SBOID", example = "ch:1:sboid:100001", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private String businessOrganisation;

  @Schema(description = "Comment", example = "Comment regarding the line", accessMode = AccessMode.READ_ONLY)
  private String comment;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5",
      accessMode = AccessMode.READ_ONLY)
  private Integer etagVersion;

}


