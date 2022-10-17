package ch.sbb.workflow.api;

import ch.sbb.workflow.entity.BusinessObjectType;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.entity.WorkflowStatus;
import ch.sbb.workflow.entity.WorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDateTime;
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
@Schema(name = "Workflow")
public class WorkflowModel {

  @Schema(description = "Business Object Type", example = "SLNID")
  @NotNull
  private BusinessObjectType businessObjectType;

  @Schema(description = "Business Object Id: the generated DB id")
  @NotNull
  private Long businessObjectId;

  @Schema(description = "Swiss Id: the generated DB id", example = "CHLNR")
  @NotBlank
  private String swissId;

  @Schema(description = "Workflow Type", example = "LINE")
  @NotNull
  private WorkflowType workflowType;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private WorkflowStatus workflowStatus;

  @Schema(description = "Client")
  private PersonModel client;

  @Schema(description = "Examinant")
  private PersonModel examinant;

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

  public static Workflow toEntity(WorkflowModel model) {
    return Workflow.builder()
        .workflowType(model.getWorkflowType())
        .businessObjectId(model.getBusinessObjectId())
        .businessObjectType(model.getBusinessObjectType())
        .swissId(model.getSwissId())
        .client(PersonModel.toEntity(model.getClient()))
        .examinant(PersonModel.toEntity(model.getExaminant()))
        .build();
  }

  public static WorkflowModel toModel(Workflow entity) {
    return WorkflowModel.builder()
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .businessObjectType(entity.getBusinessObjectType())
        .swissId(entity.getSwissId())
        .workflowStatus(entity.getStatus())
        .client(PersonModel.toModel(entity.getClient()))
        .examinant(PersonModel.toModel(entity.getExaminant()))
        .build();
  }

}
