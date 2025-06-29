package ch.sbb.workflow.sepodi.hearing.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.workflow.BasePersonModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(name = "StopPointRestartWorkflow")
public class StopPointRestartWorkflowModel extends BasePersonModel {

  @Schema(description = "Organisation", example = "ZVV Zürcher Verkehrsverbund")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String organisation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "Restart motivation")
  private String motivationComment;

  @NotNull
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_30)
  @Schema(description = "Official designation of a location that must be used by all recipients"
          , example = "Biel/Bienne Bözingenfeld/Champ", maxLength = 30)
  private String designationOfficial;

  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Schema(description = "mail", example = "mail@sbb.ch")
  @NotBlank
  private String mail;
}
