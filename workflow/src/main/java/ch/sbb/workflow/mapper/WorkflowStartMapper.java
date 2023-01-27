package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.WorkflowStartModel;
import ch.sbb.workflow.entity.Workflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkflowStartMapper {

  public static Workflow toEntity(WorkflowStartModel model) {
    return Workflow.builder()
        .workflowType(model.getWorkflowType())
        .businessObjectId(model.getBusinessObjectId())
        .swissId(model.getSwissId())
        .description(model.getDescription())
        .workflowComment(model.getWorkflowComment())
        .client(ClientPersonMapper.toEntity(model.getClient()))
        .build();
  }

}
