package ch.sbb.workflow.api;

import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "WorkflowCheck")
public class WorkflowCheckModel {

  @Schema(description = "Workflow Object Id: the generated DB id")
  @NotNull
  private Long workflowId;

  @Schema(description = "Workflow Type", example = "LINE")
  @NotNull
  private WorkflowType workflowType;

  private String checkComment;

  @Schema(description = "Examinant")
  private PersonModel examinant;

  public static Workflow toEntity(WorkflowCheckModel model) {
    return Workflow.builder()
        .id(model.workflowId)
        .workflowType(model.getWorkflowType())
        .checkComment(model.getCheckComment())
        .examinant(PersonModel.toEntity(model.getExaminant()))
        .build();
  }

  public static WorkflowCheckModel toModel(Workflow entity) {
    return WorkflowCheckModel.builder()
        .workflowType(entity.getWorkflowType())
        .checkComment(entity.getCheckComment())
        .examinant(PersonModel.toModel(entity.getExaminant()))
        .build();

  }

}
