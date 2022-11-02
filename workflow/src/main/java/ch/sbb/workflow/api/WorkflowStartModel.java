package ch.sbb.workflow.api;

import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

  @Schema(description = "Swiss Id: the generated DB id", example = "CHLNR")
  @NotBlank
  private String swissId;

  @Schema(description = "Workflow Type", example = "LINE")
  @NotNull
  private WorkflowType workflowType;

  @NotNull
  private String description;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  private WorkflowStatus workflowStatus;

  private String workflowComment;

  @Schema(description = "Client")
  private PersonModel client;

  public static Workflow toEntity(WorkflowStartModel model) {
    return Workflow.builder()
        .workflowType(model.getWorkflowType())
        .businessObjectId(model.getBusinessObjectId())
        .swissId(model.getSwissId())
        .description(model.getDescription())
        .workflowComment(model.getWorkflowComment())
        .client(PersonModel.toEntity(model.getClient()))
        .build();
  }

  public static WorkflowStartModel toModel(Workflow entity) {
    return WorkflowStartModel.builder()
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .swissId(entity.getSwissId())
        .description(entity.getDescription())
        .workflowStatus(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .client(PersonModel.toModel(entity.getClient()))
        .build();
  }

}
