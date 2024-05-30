package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.WorkflowStartModel;
import ch.sbb.workflow.entity.LineWorkflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkflowStartMapper {

  public static LineWorkflow toEntity(WorkflowStartModel model) {
    return LineWorkflow.builder()
        .workflowType(model.getWorkflowType())
        .businessObjectId(model.getBusinessObjectId())
        .swissId(model.getSwissId())
        .description(model.getDescription())
        .workflowComment(model.getWorkflowComment())
        .client(ClientPersonMapper.toEntity(model.getClient()))
        .build();
  }

}
