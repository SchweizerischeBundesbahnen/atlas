package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.api.StopPointWorkflowApiV1;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StopPointWorkflowController implements StopPointWorkflowApiV1 {

  private final StopPointWorkflowService service;


  @Override
  public ReadStopPointWorkflowModel getStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toReadModel(service.getWorkflow(id));
  }

  @Override
  public List<ReadStopPointWorkflowModel> getStopPointWorkflows() {
    return service.getWorkflows().stream().map(StopPointWorkflowMapper::toReadModel).toList();
  }

  @Override
  public ReadStopPointWorkflowModel addStopPointWorkflow(StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toReadModel(service.addWorkflow(workflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel startStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toReadModel(service.startWorkflow(id));
  }

  @Override
  public ReadStopPointWorkflowModel editStopPointWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toReadModel(service.editWorkflow(id,workflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel rejectStopPointWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toReadModel(service.rejectWorkflow(id,workflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel addExaminantToStopPointWorkflow(Long id, ClientPersonModel personModel) {
    return StopPointWorkflowMapper.toReadModel(service.addExaminantToWorkflow(id,personModel));
  }

  @Override
  public ReadStopPointWorkflowModel removeExaminantFromStopPointWorkflow(Long id, Long personId) {
    return StopPointWorkflowMapper.toReadModel(service.removeExaminantToWorkflow(id,personId));
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
  public ReadStopPointWorkflowModel restartStopPointWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    return StopPointWorkflowMapper.toReadModel(service.restartWorkflow(id,restartWorkflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel cancelStopPointWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    return StopPointWorkflowMapper.toReadModel(service.cancelWorkflow(id,stopPointCancelWorkflowModel));
  }

}
