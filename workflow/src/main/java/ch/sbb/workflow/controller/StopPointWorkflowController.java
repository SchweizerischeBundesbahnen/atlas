package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.workflow.api.StopPointWorkflowApiV1;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowService;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowTransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StopPointWorkflowController implements StopPointWorkflowApiV1 {

  private final StopPointWorkflowService service;
  private final StopPointWorkflowTransitionService workflowTransitionService;


  @Override
  public ReadStopPointWorkflowModel getStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.getWorkflow(id));
  }

  @Override
  public Container<ReadStopPointWorkflowModel> getStopPointWorkflows(Pageable pageable, StopPointWorkflowRequestParams stopPointWorkflowRequestParams) {
    StopPointWorkflowSearchRestrictions stopPointWorkflowSearchRestrictions = StopPointWorkflowSearchRestrictions.builder()
            .pageable(pageable)
            .stopPointWorkflowRequestParams(stopPointWorkflowRequestParams)
            .build();
    Page<StopPointWorkflow> workflows = service.getWorkflows(stopPointWorkflowSearchRestrictions);

    return Container.<ReadStopPointWorkflowModel>builder()
            .objects(workflows.stream().map(StopPointWorkflowMapper::toModel).toList())
            .totalCount(workflows.getTotalElements())
            .build();
  }

  @Override
  public ReadStopPointWorkflowModel addStopPointWorkflow(StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.addWorkflow(workflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel startStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.startWorkflow(id));
  }

  @Override
  public ReadStopPointWorkflowModel rejectStopPointWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.rejectWorkflow(id, workflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel editStopPointWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.editWorkflow(id, workflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel addExaminantToStopPointWorkflow(Long id, StopPointClientPersonModel personModel) {
    return StopPointWorkflowMapper.toModel(service.addExaminantToWorkflow(id, personModel));
  }

  @Override
  public ReadStopPointWorkflowModel removeExaminantFromStopPointWorkflow(Long id, Long personId) {
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
  public ReadStopPointWorkflowModel restartStopPointWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.restartWorkflow(id, restartWorkflowModel));
  }

  @Override
  public ReadStopPointWorkflowModel cancelStopPointWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.cancelWorkflow(id, stopPointCancelWorkflowModel));
  }

}
