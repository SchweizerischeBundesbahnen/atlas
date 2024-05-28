package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "StopPointRejectWorkflow")
public class StopPointRejectWorkflowModel {

  @Schema(description = "BAV Examinant Client")
  @NotNull
  private ClientPersonModel examinantBAVClient;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "Reject motivation")
  private String motivationComment;

}
