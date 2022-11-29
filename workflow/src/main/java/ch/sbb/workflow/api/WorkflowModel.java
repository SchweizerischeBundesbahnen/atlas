package ch.sbb.workflow.api;

import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDateTime;
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
@Schema(name = "Workflow")
public class WorkflowModel {

  @Schema(description = "Workflow ID", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private Long id;

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

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  private String workflowComment;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  private String checkComment;

  @Schema(description = "Client")
  private ClientPersonModel client;

  @Schema(description = "Examinant")
  private PersonModel examinant;

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

  public static WorkflowModel toModel(Workflow entity) {
    WorkflowModelBuilder builder = WorkflowModel.builder()
        .id(entity.getId())
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .swissId(entity.getSwissId())
        .description(entity.getDescription())
        .workflowStatus(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .checkComment(entity.getCheckComment())
        .creationDate(entity.getCreationDate())
        .editionDate(entity.getEditionDate());
    if (entity.getClient() != null) {
      builder.client(ClientPersonModel.toModel(entity.getClient()));
    }
    if (entity.getExaminant() != null) {
      builder.examinant(PersonModel.toModel(entity.getExaminant()));
    }

    return builder.build();
  }

  public static WorkflowModel toNewModel(Workflow entity) {
    return WorkflowModel.builder()
        .id(entity.getId())
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .swissId(entity.getSwissId())
        .description(entity.getDescription())
        .workflowStatus(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .checkComment(entity.getCheckComment())
        .client(ClientPersonModel.toModel(entity.getClient()))
        .creationDate(entity.getCreationDate())
        .editionDate(entity.getEditionDate())
        .build();
  }

}
