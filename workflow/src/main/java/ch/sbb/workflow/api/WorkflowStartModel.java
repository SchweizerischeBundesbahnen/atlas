package ch.sbb.workflow.api;

import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
  @NotBlank
  private String swissId;

  @Schema(description = "Workflow Type", example = "LINE")
  @NotNull
  private WorkflowType workflowType;

  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String description;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  private WorkflowStatus workflowStatus;

  @Schema(description = "Comment accompanying the start of the workflow")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  private String workflowComment;

  @Schema(description = "Client")
  @Valid
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
