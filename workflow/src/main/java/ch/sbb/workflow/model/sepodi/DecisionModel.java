package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.workflow.entity.JudgementType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "Decision")
public class DecisionModel extends OtpVerificationModel {

  @Schema(description = "Judgement")
  @NotNull
  private JudgementType judgement;

  @Schema(description = "Motivation", example = "I agree")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String motivation;

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

  @Schema(description = "Organisation", example = "ZVV Zürcher Verkehrsverbund")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String organisation;

  @Schema(description = "Person Function", example = "Officer")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String personFunction;

  @JsonIgnore
  @AssertTrue(message = "Motivation must not be null if Judgement is NO")
  public boolean isMotivationNeeded() {
    return judgement != JudgementType.NO || motivation != null;
  }

}
