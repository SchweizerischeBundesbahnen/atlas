package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "TerminationDecision")
public class TerminationDecisionModel {

  @Schema(description = "Judgement")
  @NotNull
  private JudgementType judgement;

  @Schema(description = "Motivation", example = "I agree")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  private String motivation;

  @Schema(description = "Decision by person: Info+ or NOVA")
  @NotNull
  private TerminationDecisionPerson terminationDecisionPerson;

  @Schema(description = "Object creation date", example = "01.01.2000")
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String creator;

  @Schema(description = "Last edition date", example = "01.01.2000")
  private LocalDateTime editionDate;

  @Schema(description = "Editor", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String editor;

}
