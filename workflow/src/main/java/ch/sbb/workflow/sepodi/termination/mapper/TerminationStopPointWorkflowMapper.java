package ch.sbb.workflow.sepodi.termination.mapper;

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
        .applicantMail(model.getApplicantMail())
        .status(model.getStatus())
        .workflowComment(model.getWorkflowComment())
        .boTerminationDate(model.getBoTerminationDate())
        .infoPlusTerminationDate(model.getInfoPlusTerminationDate())
        .novaTerminationDate(model.getNovaTerminationDate())
        .infoPlusDecision(model.getInfoPlusDecision() != null ?
            TerminationDecisionMapper.toEntity(model.getInfoPlusDecision()) : null)
        .build();
  }

  public static TerminationStopPointWorkflow toEntityStart(StartTerminationStopPointWorkflowModel model) {
    return TerminationStopPointWorkflow.builder()
        .versionId(model.getVersionId())
        .sloid(model.getSloid())
        .applicantMail(model.getApplicantMail())
        .boTerminationDate(model.getBoTerminationDate())
        .build();
  }

  public static TerminationStopPointWorkflowModel toModel(TerminationStopPointWorkflow workflow) {
    return TerminationStopPointWorkflowModel.builder()
        .id(workflow.getId())
        .versionId(workflow.getVersionId())
        .applicantMail(workflow.getApplicantMail())
        .sloid(workflow.getSloid())
        .status(workflow.getStatus())
        .workflowComment(workflow.getWorkflowComment())
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
