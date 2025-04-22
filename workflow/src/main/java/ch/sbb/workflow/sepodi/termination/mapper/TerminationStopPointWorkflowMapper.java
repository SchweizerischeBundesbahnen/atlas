package ch.sbb.workflow.sepodi.termination.mapper;

import ch.sbb.workflow.mapper.ClientPersonMapper;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationStopPointWorkflowMapper {

  public static TerminationStopPointWorkflow toEntity(TerminationStopPointWorkflowModel model) {
    return TerminationStopPointWorkflow.builder()
        .versionId(model.getVersionId())
        .sloid(model.getSloid())
        .sboid(model.getSboid())
        .applicantMail(model.getApplicantMail())
        .status(model.getStatus())
        .boTerminationDate(model.getBoTerminationDate())
        .infoPlusTerminationDate(model.getInfoPlusTerminationDate())
        .novaTerminationDate(model.getNovaTerminationDate())
        .infoPlusExaminant(ClientPersonMapper.toEntity(model.getInfoPlusExaminant()))
        .novaExaminant(ClientPersonMapper.toEntity(model.getNovaExaminant()))
        .build();
  }

  public static TerminationStopPointWorkflow toEntityStart(StartTerminationStopPointWorkflowModel model) {
    return TerminationStopPointWorkflow.builder()
        .versionId(model.getVersionId())
        .sloid(model.getSloid())
        .sboid(model.getSboid())
        .applicantMail(model.getApplicantMail())
        .boTerminationDate(model.getBoTerminationDate())
        .infoPlusExaminant(ClientPersonMapper.toEntity(model.getInfoPlusExaminant()))
        .build();
  }

  public static TerminationStopPointWorkflowModel toModel(TerminationStopPointWorkflow workflow) {
    return TerminationStopPointWorkflowModel.builder()
        .id(workflow.getId())
        .versionId(workflow.getVersionId())
        .applicantMail(workflow.getApplicantMail())
        .sboid(workflow.getSboid())
        .sloid(workflow.getSloid())
        .status(workflow.getStatus())
        .boTerminationDate(workflow.getBoTerminationDate())
        .infoPlusTerminationDate(workflow.getInfoPlusTerminationDate())
        .novaTerminationDate(workflow.getNovaTerminationDate())
        .infoPlusExaminant(ClientPersonMapper.toModel(workflow.getInfoPlusExaminant()))
        .novaExaminant(ClientPersonMapper.toModel(workflow.getNovaExaminant()))
        .creationDate(workflow.getCreationDate())
        .creator(workflow.getCreator())
        .editor(workflow.getEditor())
        .editionDate(workflow.getEditionDate())
        .build();
  }

}
