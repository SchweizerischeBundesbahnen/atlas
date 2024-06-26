package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
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
@Schema(name = "OverrideDecision")
public class OverrideDecisionModel {

  @Schema(description = "Firstname", example = "John")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String firstName;

  @Schema(description = "Second", example = "Doe")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String lastName;

  @Schema(description = "BAV Judgement", example = "true")
  @NotNull
  private JudgementType fotJudgement;

  @Schema(description = "Motivation", example = "I agree")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String fotMotivation;

}
