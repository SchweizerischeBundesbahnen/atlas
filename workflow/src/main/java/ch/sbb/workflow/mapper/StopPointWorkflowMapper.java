package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.StopPointWorkflowStartModel;
import ch.sbb.workflow.entity.StopPointWorkflow;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowMapper {

  public static StopPointWorkflowStartModel toModel(StopPointWorkflow entity) {
    return StopPointWorkflowStartModel.builder()
        .sloid(entity.getSloid())
        .examinants(entity.getExaminants().stream().map(ClientPersonMapper::toModel).toList())
        .sboid(entity.getSboid())
        .designationOfficial(entity.getDesignationOfficial())
        .swissMunicipalityName(entity.getSwissMunicipalityName())
        .workflowComment(entity.getWorkflowComment())
        .build();
  }

  //startStopPointModel
  public static StopPointWorkflow toEntity(StopPointWorkflowStartModel model) {
    return StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .sboid(model.getSboid())
        .swissMunicipalityName(model.getSwissMunicipalityName())
        .designationOfficial(model.getDesignationOfficial())
        .startDate(model.getValidFrom())
        .endDate(model.getValidTo())
        .workflowComment(model.getWorkflowComment())
        .examinants(model.getExaminants().stream().map(ClientPersonMapper::toEntity).collect(Collectors.toSet()))
        .build();
  }

}
