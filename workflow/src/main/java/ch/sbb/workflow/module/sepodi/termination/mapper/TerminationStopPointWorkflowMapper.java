package ch.sbb.workflow.module.sepodi.termination.mapper;

import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationExaminants.InfoPlus;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationExaminants.Nova;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
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
        .workflowComment(model.getWorkflowComment())
        .build();
  }

  public static TerminationStopPointWorkflowModel toModel(TerminationStopPointWorkflow workflow, InfoPlus infoPlus, Nova nova) {
    TerminationStopPointWorkflowModel model = toModel(workflow);
    TerminationDecisionModel infoPlusDecision = model.getInfoPlusDecision();
    infoPlusDecision.setExaminantMail(infoPlus.getEmail());
    infoPlusDecision.setFirstName(infoPlus.getFirstname());
    infoPlusDecision.setLastName(infoPlus.getLastname());
    infoPlusDecision.setOrganisation(infoPlus.getOrganisation());
    infoPlusDecision.setTerminationDate(workflow.getInfoPlusTerminationDate());

    TerminationDecisionModel novaDecision = model.getNovaDecision();
    novaDecision.setExaminantMail(nova.getEmail());
    novaDecision.setFirstName(nova.getFirstname());
    novaDecision.setLastName(nova.getLastname());
    novaDecision.setOrganisation(nova.getOrganisation());
    novaDecision.setTerminationDate(workflow.getNovaTerminationDate());

    model.setInfoPlusDecision(infoPlusDecision);
    model.setNovaDecision(novaDecision);
    model.setAbortComment(workflow.getAbortComment());
    model.setVersionValidTo(workflow.getVersionValidTo());

    return model;
  }

  public static TerminationStopPointWorkflowModel toModel(TerminationStopPointWorkflow workflow) {
    return TerminationStopPointWorkflowModel.builder()
        .id(workflow.getId())
        .versionId(workflow.getVersionId())
        .applicantMail(workflow.getApplicantMail())
        .designationOfficial(workflow.getDesignationOfficial())
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
