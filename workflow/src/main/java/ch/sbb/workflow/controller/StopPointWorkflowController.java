package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.DecisionModel;
import ch.sbb.atlas.api.workflow.OverrideDecisionModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.api.workflow.StopPointRejectWorkflowModel;
import ch.sbb.atlas.api.workflow.StopPointRestartWorkflowModel;
import ch.sbb.workflow.api.StopPointWorkflowApiV1;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.service.StopPointWorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StopPointWorkflowController implements StopPointWorkflowApiV1 {

  private final StopPointWorkflowService service;


  @Override
  public StopPointAddWorkflowModel getStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.getWorkflow(id));
  }

  @Override
  public List<StopPointAddWorkflowModel> getStopPointWorkflows() {
    return service.getWorkflows().stream().map(StopPointWorkflowMapper::toModel).toList();
  }

  @Override
  public StopPointAddWorkflowModel addStopPointWorkflow(StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.addWorkflow(workflowModel));
  }

  @Override
  public StopPointAddWorkflowModel startStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.startWorkflow(id));
  }

  @Override
  public StopPointAddWorkflowModel editStopPointWorkflow(Long id, StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.editWorkflow(id,workflowModel));
  }

  @Override
  public StopPointAddWorkflowModel rejectStopPointWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.rejectWorkflow(id,workflowModel));
  }

  @Override
  public StopPointAddWorkflowModel addExaminantToStopPointWorkflow(Long id, ClientPersonModel personModel) {
    return StopPointWorkflowMapper.toModel(service.addExaminantToWorkflow(id,personModel));
  }

  @Override
  public StopPointAddWorkflowModel removeExaminantFromStopPointWorkflow(Long id, Long personId) {
    return StopPointWorkflowMapper.toModel(service.removeExaminantToWorkflow(id,personId));
  }

  @Override
  public void obtainOtpForStopPointWorkflow(Long id, Long personId) {
    service.obtainOtp(id,personId);
  }

  @Override
  public void voteWorkflow(Long id, Long personId, DecisionModel decisionModel) {
    service.voteWorkFlow(id, personId,decisionModel);
  }

  @Override
  public void overrideVoteWorkflow(Long id, Long personId, OverrideDecisionModel decisionModel) {
    service.overrideVoteWorkflow(id, personId,decisionModel);
  }

  @Override
  public StopPointAddWorkflowModel restartStopPointWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    return StopPointWorkflowMapper.toModel(service.restartWorkflow(id,restartWorkflowModel));
  }

  @Override
  public StopPointAddWorkflowModel cancelStopPointWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    return StopPointWorkflowMapper.toModel(service.cancelWorkflow(id,stopPointCancelWorkflowModel));
  }

}
