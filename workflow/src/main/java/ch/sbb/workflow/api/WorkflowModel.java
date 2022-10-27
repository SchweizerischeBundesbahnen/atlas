package ch.sbb.workflow.api;

import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
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

  private String checkComment;

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
        .swissId(model.getSwissId())
        .description(model.getDescription())
        .workflowComment(model.getWorkflowComment())
        .checkComment(model.getCheckComment())
        .client(PersonModel.toEntity(model.getClient()))
        .examinant(PersonModel.toEntity(model.getExaminant()))
        .build();
  }

  public static WorkflowModel toModel(Workflow entity) {
    return WorkflowModel.builder()
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .swissId(entity.getSwissId())
        .description(entity.getDescription())
        .workflowStatus(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .checkComment(entity.getCheckComment())
        .client(PersonModel.toModel(entity.getClient()))
        .examinant(PersonModel.toModel(entity.getExaminant()))
        .creationDate(entity.getCreationDate())
        .editionDate(entity.getEditionDate())
        .build();
  }

}
