package ch.sbb.workflow.sepodi.termination.mapper;

import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
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
        .infoPlusDecision(model.getInfoPlusDecision() != null ?
            TerminationDecisionMapper.toEntity(model.getInfoPlusDecision()) : null)
        .build();
  }

  public static TerminationStopPointWorkflow toEntityStart(TerminationStopPointWorkflowModel model) {
    return TerminationStopPointWorkflow.builder()
        .versionId(model.getVersionId())
        .sloid(model.getSloid())
        .sboid(model.getSboid())
        .applicantMail(model.getApplicantMail())
        .boTerminationDate(model.getBoTerminationDate())
        .infoPlusDecision(TerminationDecisionMapper.toEntity(model.getInfoPlusDecision()))
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
        .infoPlusDecision(workflow.getInfoPlusDecision() != null ?
            TerminationDecisionMapper.toModel(workflow.getInfoPlusDecision()) : null)
        .novaDecision(workflow.getNovaDecision() != null ? TerminationDecisionMapper.toModel(workflow.getNovaDecision()) : null)
        .creationDate(workflow.getCreationDate())
        .creator(workflow.getCreator())
        .editor(workflow.getEditor())
        .editionDate(workflow.getEditionDate())
        .build();
  }

}
