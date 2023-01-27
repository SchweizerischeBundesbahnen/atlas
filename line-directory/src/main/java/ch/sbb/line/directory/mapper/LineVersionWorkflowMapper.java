package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.line.LineVersionWorkflowModel;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionWorkflowMapper {

  public static LineVersionWorkflowModel toModel(LineVersionWorkflow lineVersionWorkflow) {
    return LineVersionWorkflowModel.builder()
        .workflowId(lineVersionWorkflow.getWorkflowId())
        .workflowProcessingStatus(lineVersionWorkflow.getWorkflowProcessingStatus())
        .build();
  }

}
