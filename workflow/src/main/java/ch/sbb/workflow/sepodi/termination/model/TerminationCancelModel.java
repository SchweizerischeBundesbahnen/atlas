package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(name = "TerminationCancel")
public class TerminationCancelModel {

  @Schema(description = "Cancel model", example = "I don't agree")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @NotNull
  private String cancelComment;

}
