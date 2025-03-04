package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.WorkflowModel;
import ch.sbb.atlas.api.workflow.WorkflowModel.WorkflowModelBuilder;
import ch.sbb.workflow.entity.LineWorkflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineWorkflowMapper {

  public static WorkflowModel toModel(LineWorkflow entity) {
    WorkflowModelBuilder builder = WorkflowModel.builder()
        .id(entity.getId())
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .swissId(entity.getSwissId())
        .number(entity.getNumber())
        .description(entity.getDescription())
        .workflowStatus(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .checkComment(entity.getCheckComment())
        .creationDate(entity.getCreationDate())
        .editionDate(entity.getEditionDate());
    if (entity.getClient() != null) {
      builder.client(ClientPersonMapper.toModel(entity.getClient()));
    }
    if (entity.getExaminant() != null) {
      builder.examinant(PersonMapper.toModel(entity.getExaminant()));
    }

    return builder.build();
  }

  public static WorkflowModel toNewModel(LineWorkflow entity) {
    return WorkflowModel.builder()
        .id(entity.getId())
        .workflowType(entity.getWorkflowType())
        .businessObjectId(entity.getBusinessObjectId())
        .swissId(entity.getSwissId())
        .number(entity.getNumber())
        .description(entity.getDescription())
        .workflowStatus(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .checkComment(entity.getCheckComment())
        .client(ClientPersonMapper.toModel(entity.getClient()))
        .creationDate(entity.getCreationDate())
        .editionDate(entity.getEditionDate())
        .build();
  }

}
