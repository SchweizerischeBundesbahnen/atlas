package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.workflow.entity.StopPointWorkflow;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowMapper {

  public static StopPointAddWorkflowModel toModel(StopPointWorkflow entity) {
    return StopPointAddWorkflowModel.builder()
        .sloid(entity.getSloid())
        .examinants(entity.getExaminants().stream().map(ClientPersonMapper::toModel).toList())
        .ccEmails(entity.getCcEmails())
        .sboid(entity.getSboid())
        .validFrom(entity.getStartDate())
        .validTo(entity.getEndDate())
        .versionId(entity.getVersionId())
        .designationOfficial(entity.getDesignationOfficial())
        .swissMunicipalityName(entity.getSwissMunicipalityName())
        .status(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .build();
  }

  //startStopPointModel
  public static StopPointWorkflow toEntity(StopPointAddWorkflowModel model, List<ClientPersonModel> examinants) {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .sboid(model.getSboid())
        .swissMunicipalityName(model.getSwissMunicipalityName())
        .designationOfficial(model.getDesignationOfficial())
        .startDate(model.getValidFrom())
        .endDate(model.getValidTo())
        .ccEmails(model.getCcEmails())
        .workflowComment(model.getWorkflowComment())
        .build();
    model.getExaminants().addAll(examinants);
    stopPointWorkflow.setExaminants(
        model.getExaminants().stream().map(clientPersonModel -> ClientPersonMapper.toEntity(clientPersonModel, stopPointWorkflow))
            .collect(Collectors.toSet()));
    return stopPointWorkflow;
  }

  public static StopPointWorkflow toEntity(StopPointAddWorkflowModel model) {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .sboid(model.getSboid())
        .swissMunicipalityName(model.getSwissMunicipalityName())
        .designationOfficial(model.getDesignationOfficial())
        .startDate(model.getValidFrom())
        .endDate(model.getValidTo())
        .ccEmails(model.getCcEmails())
        .workflowComment(model.getWorkflowComment())
        .build();
    stopPointWorkflow.setExaminants(
        model.getExaminants().stream().map(clientPersonModel -> ClientPersonMapper.toEntity(clientPersonModel, stopPointWorkflow))
            .collect(Collectors.toSet()));
    return stopPointWorkflow;
  }

}
