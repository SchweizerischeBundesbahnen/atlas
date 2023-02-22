package ch.sbb.atlas.api.workflow;

import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ExaminantWorkflowCheck")
public class ExaminantWorkflowCheckModel {

  @Schema(description = "Workflow was acceptable to the BAV")
  private boolean accepted;

  @Schema(description = "Comment from the BAV for the workflow progress")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String checkComment;

  @NotNull
  @Valid
  @Schema(description = "Examinant")
  private PersonModel examinant;

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "Examinant did not accept without comment")
  boolean isDeclinedCommentIsNecessary() {
    return accepted || StringUtils.isNotBlank(checkComment);
  }

}
