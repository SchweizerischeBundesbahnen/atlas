package ch.sbb.workflow.mapper;

import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
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
        .examinants(entity.getExaminants().stream().map(StopPointClientPersonMapper::toModel).toList())
        .designationOfficial(entity.getDesignationOfficial())
        .ccEmails(entity.getCcEmails())
        .status(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .localityName(entity.getLocalityName())
        .versionValidFrom(entity.getVersionValidFrom())
        .createdAt(entity.getCreationDate())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .build();
  }

  public static StopPointWorkflow toEntity(StopPointAddWorkflowModel model, List<StopPointClientPersonModel> examinants) {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .ccEmails(model.getCcEmails())
        .workflowComment(model.getWorkflowComment())
        .designationOfficial(model.getDesignationOfficial())
        .localityName(model.getLocalityName())
        .versionValidFrom(model.getVersionValidFrom())
        .status(model.getStatus())
        .build();
    model.getExaminants().addAll(examinants);
    stopPointWorkflow.setExaminants(
        model.getExaminants().stream().map(StopPointClientPersonMapper::toEntity)
            .collect(Collectors.toSet()));
    return stopPointWorkflow;
  }

}
