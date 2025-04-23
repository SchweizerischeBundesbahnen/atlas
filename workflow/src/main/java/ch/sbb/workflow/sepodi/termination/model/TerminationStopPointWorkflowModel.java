package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "TerminationStopPointAddWorkflow")
public class TerminationStopPointWorkflowModel {

  @Schema(description = "Workflow id", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Service Point version id")
  @NotNull
  private Long versionId;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @NotEmpty
  @Schema(description = "Applicant mail", example = "me@you.ch")
  private String applicantMail;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  @NotNull
  private String sloid;

  @NotNull
  @Schema(description = "Service Point version SBOID")
  private String sboid;

  @Schema(description = "Termination Workflow Status", accessMode = AccessMode.READ_ONLY)
  private TerminationWorkflowStatus status;

  @Schema(description = "Termination Date defined by Business Organisation managing the Stop Point")
  private LocalDate boTerminationDate;

  @Schema(description = "Termination Date defined by Info Plus")
  private LocalDate infoPlusTerminationDate;

  @Schema(description = "Termination Date defined by NOVA")
  private LocalDate novaTerminationDate;

  @Schema(description = "Info Plus Decision")
  private TerminationDecisionModel infoPlusDecision;

  @Schema(description = "Nova Decision")
  private TerminationDecisionModel novaDecision;

  @Schema(description = "Object creation date", example = "01.01.2000")
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String creator;

  @Schema(description = "Last edition date", example = "01.01.2000")
  private LocalDateTime editionDate;

  @Schema(description = "Editor", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String editor;

}
