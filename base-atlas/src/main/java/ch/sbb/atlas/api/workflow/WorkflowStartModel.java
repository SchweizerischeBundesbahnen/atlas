package ch.sbb.atlas.api.workflow;

import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.atlas.base.service.model.workflow.WorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
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
@Schema(name = "WorkflowStart")
public class WorkflowStartModel {

  @Schema(description = "Business Object Id: the generated DB id")
  @NotNull
  private Long businessObjectId;

  @Schema(description = "Swiss Id: the SwissLineNumber used to map Atlas object to the Workflow", example = "b1.L1")
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String swissId;

  @Schema(description = "Workflow Type", example = "LINE")
  @NotNull
  private WorkflowType workflowType;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String description;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  private WorkflowStatus workflowStatus;

  @Schema(description = "Comment accompanying the start of the workflow")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  private String workflowComment;

  @Schema(description = "Client")
  @Valid
  @NotNull
  private ClientPersonModel client;

}
