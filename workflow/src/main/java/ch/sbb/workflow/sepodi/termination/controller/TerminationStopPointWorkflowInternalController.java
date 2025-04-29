package ch.sbb.workflow.sepodi.termination.controller;

import ch.sbb.workflow.exception.TerminationDecisionPersonException;
import ch.sbb.workflow.sepodi.termination.api.TerminationStopPointWorkflowApi;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TerminationStopPointWorkflowInternalController implements TerminationStopPointWorkflowApi {

  private final TerminationStopPointWorkflowService service;

  @Override
  public TerminationStopPointWorkflowModel getTerminationStopPointWorkflow(Long id) {
    return TerminationStopPointWorkflowMapper.toModel(service.getTerminationWorkflow(id));
  }

  @Override
  public TerminationStopPointWorkflowModel startTerminationStopPointWorkflow(
      TerminationStopPointWorkflowModel workflowModel) {
    return TerminationStopPointWorkflowMapper.toModel(service.startTerminationWorkflow(workflowModel));
  }

  @Override
  public TerminationStopPointWorkflowModel decisionInfoPlus(TerminationDecisionModel decisionModel, Long workflowId) {
    if (decisionModel.getTerminationDecisionPerson() != TerminationDecisionPerson.INFO_PLUS) {
      throw new TerminationDecisionPersonException(TerminationDecisionPerson.INFO_PLUS);
    }
    return TerminationStopPointWorkflowMapper.toModel(service.addDecisionInfoPlus(decisionModel, workflowId));
  }

}
