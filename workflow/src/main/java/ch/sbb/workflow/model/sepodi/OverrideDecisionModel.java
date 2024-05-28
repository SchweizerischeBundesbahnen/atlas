package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.entity.JudgementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(name = "Decision")
public class OverrideDecisionModel {

  @Schema(description = "Override Examinant")
  @NotNull
  private ClientPersonModel overrideExaminant;

  @Schema(description = "BAV Judgement", example = "true")
  @NotNull
  private JudgementType fotJudgement;

  @Schema(description = "Motivation", example = "I'm agree")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String fotMotivation;

}
