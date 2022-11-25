package ch.sbb.workflow.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
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
  private String checkComment;

  @NotNull
  @Schema(description = "Examinant")
  private PersonModel examinant;

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "Examinant did not accept without comment")
  boolean isDeclinedCommentIsNecessary() {
    return accepted || StringUtils.isNotBlank(checkComment);
  }

}
