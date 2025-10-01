package ch.sbb.workflow.module.sepodi.termination.controller;

import static ch.sbb.workflow.module.sepodi.termination.TerminationWorkflowHelper.calculateTerminationDate;

import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import ch.sbb.workflow.module.sepodi.termination.api.TerminationStopPointWorkflowApiInternal;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.exception.TerminationDecisionPersonException;
import ch.sbb.workflow.module.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationInfoModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.service.TerminationStopPointWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TerminationStopPointWorkflowApiInternalController implements TerminationStopPointWorkflowApiInternal {

  private final TerminationStopPointWorkflowService service;
  private final TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService;

  @Override
  public TerminationInfoModel getTerminationInfoBySloid(String sloid) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    TerminationStopPointWorkflow terminationWorkflow = service.getTerminationWorkflowBySloidAndInProgress(sloid);
    return calculateTerminationDate(terminationWorkflow);
  }

  @Redacted
  @Override
  public TerminationStopPointWorkflowModel decisionInfoPlus(TerminationDecisionModel decisionModel, Long workflowId) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    if (decisionModel.getTerminationDecisionPerson() != TerminationDecisionPerson.INFO_PLUS) {
      throw new TerminationDecisionPersonException(TerminationDecisionPerson.INFO_PLUS);
    }
    return TerminationStopPointWorkflowMapper.toModel(service.addDecisionInfoPlus(decisionModel, workflowId));
  }

  @Redacted
  @Override
  public TerminationStopPointWorkflowModel decisionNova(TerminationDecisionModel decisionModel, Long workflowId) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    if (decisionModel.getTerminationDecisionPerson() != TerminationDecisionPerson.NOVA) {
      throw new TerminationDecisionPersonException(TerminationDecisionPerson.NOVA);
    }
    return TerminationStopPointWorkflowMapper.toModel(service.addDecisionNova(decisionModel, workflowId));
  }

  @Redacted
  @Override
  public TerminationStopPointWorkflowModel abortTermination(TerminationAbortModel abortModel, Long workflowId) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    return TerminationStopPointWorkflowMapper.toModel(service.abortTerminationWorkflow(workflowId, abortModel));
  }
}
