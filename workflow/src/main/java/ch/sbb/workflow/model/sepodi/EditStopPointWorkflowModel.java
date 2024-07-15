package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Schema(name = "EditStopPointWorkflow")
public class EditStopPointWorkflowModel {

  @NotNull
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_30)
  @Schema(description = "Official designation of a location that must be used by all recipients"
      , example = "Biel/Bienne Bözingenfeld/Champ", maxLength = 30)
  private String designationOfficial;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "Hearing reasons")
  private String workflowComment;

  @Schema(description = "List hearing examinants")
  private List<StopPointClientPersonModel> examinants;

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Schema(description = "List of cc emails for status of hearing")
  private List<@Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS) @Size(max = AtlasFieldLengths.LENGTH_100) String> ccEmails;

}
