package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowMapper {

  public static ReadStopPointWorkflowModel toModel(StopPointWorkflow entity) {
    return ReadStopPointWorkflowModel.builder()
        .id(entity.getId())
        .sloid(entity.getSloid())
        .versionId(entity.getVersionId())
        .examinants(entity.getExaminants().stream().map(ClientPersonMapper::toModel).toList())
        .ccEmails(entity.getCcEmails())
        .status(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .build();
  }

  public static StopPointWorkflow toEntity(StopPointAddWorkflowModel model, List<ClientPersonModel> examinants) {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .ccEmails(model.getCcEmails())
        .workflowComment(model.getWorkflowComment())
        .build();
    model.getExaminants().addAll(examinants);
    stopPointWorkflow.setExaminants(
        model.getExaminants().stream().map(ClientPersonMapper::toEntity)
            .collect(Collectors.toSet()));
    return stopPointWorkflow;
  }

}
