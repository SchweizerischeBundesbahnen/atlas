package ch.sbb.workflow.sepodi.termination.controller;

import static ch.sbb.workflow.sepodi.termination.TerminationHelper.calculateTerminationDate;

import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import ch.sbb.workflow.exception.TerminationDecisionPersonException;
import ch.sbb.workflow.sepodi.termination.api.TerminationStopPointWorkflowApi;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationInfoModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TerminationStopPointWorkflowInternalController implements TerminationStopPointWorkflowApi {

  private final TerminationStopPointWorkflowService service;
  private final TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService;

  @Override
  public TerminationStopPointWorkflowModel getTerminationStopPointWorkflow(Long id) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    return TerminationStopPointWorkflowMapper.toModel(service.getTerminationWorkflow(id));
  }

  @Override
  public TerminationInfoModel getTerminationInfoBySloid(String sloid) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    TerminationStopPointWorkflow terminationWorkflow = service.getTerminationWorkflowBySloid(sloid);
    return calculateTerminationDate(terminationWorkflow);
  }

  @Override
  public TerminationStopPointWorkflowModel startTerminationStopPointWorkflow(
      StartTerminationStopPointWorkflowModel workflowModel) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    return TerminationStopPointWorkflowMapper.toModel(service.startTerminationWorkflow(workflowModel));
  }

  @Override
  public TerminationStopPointWorkflowModel decisionInfoPlus(TerminationDecisionModel decisionModel, Long workflowId) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    if (decisionModel.getTerminationDecisionPerson() != TerminationDecisionPerson.INFO_PLUS) {
      throw new TerminationDecisionPersonException(TerminationDecisionPerson.INFO_PLUS);
    }
    return TerminationStopPointWorkflowMapper.toModel(service.addDecisionInfoPlus(decisionModel, workflowId));
  }

  @Override
  public TerminationStopPointWorkflowModel decisionNova(TerminationDecisionModel decisionModel, Long workflowId) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    if (decisionModel.getTerminationDecisionPerson() != TerminationDecisionPerson.NOVA) {
      throw new TerminationDecisionPersonException(TerminationDecisionPerson.NOVA);
    }
    return TerminationStopPointWorkflowMapper.toModel(service.addDecisionNova(decisionModel, workflowId));

  }
}
