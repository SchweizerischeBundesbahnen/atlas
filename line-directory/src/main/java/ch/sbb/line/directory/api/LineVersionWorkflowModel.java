package ch.sbb.line.directory.api;

import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.enumaration.WorkflowProcessingStatus;
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
@Schema(name = "LineVersion")
public class LineVersionWorkflowModel {

  @Schema(description = "Workflow Id", accessMode = AccessMode.READ_ONLY)
  private Long workflowId;

  @Schema(description = "WorkflowProcessingStatus")
  private WorkflowProcessingStatus workflowProcessingStatus;

  public static LineVersionWorkflowModel toModel(LineVersionWorkflow lineVersionWorkflow) {
    return LineVersionWorkflowModel.builder()
        .workflowId(lineVersionWorkflow.getWorkflowId())
        .workflowProcessingStatus(lineVersionWorkflow.getWorkflowProcessingStatus())
        .build();
  }

}
