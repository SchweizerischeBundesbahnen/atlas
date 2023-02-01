package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "LineVersionWorkflow")
public class LineVersionWorkflowModel {

  @Schema(description = "Workflow Id", accessMode = AccessMode.READ_ONLY)
  private Long workflowId;

  @Schema(description = "WorkflowProcessingStatus")
  private WorkflowProcessingStatus workflowProcessingStatus;

}
