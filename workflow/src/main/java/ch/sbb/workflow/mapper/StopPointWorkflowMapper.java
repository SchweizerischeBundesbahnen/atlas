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
        .sloid(entity.getSloid())
        .examinants(entity.getExaminants().stream().map(ClientPersonMapper::toModel).toList())
        .ccEmails(entity.getCcEmails())
        .sboid(entity.getSboid())
        .versionId(entity.getVersionId())
        .designationOfficial(entity.getDesignationOfficial())
        .swissMunicipalityName(entity.getSwissMunicipalityName())
        .status(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .build();
  }

  public static StopPointWorkflow toEntity(StopPointAddWorkflowModel model, List<ClientPersonModel> examinants) {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .sboid(model.getSboid())
        .swissMunicipalityName(model.getSwissMunicipalityName())
        .designationOfficial(model.getDesignationOfficial())
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
