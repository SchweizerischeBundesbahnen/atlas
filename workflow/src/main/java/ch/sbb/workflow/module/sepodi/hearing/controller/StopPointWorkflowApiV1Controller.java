package ch.sbb.workflow.module.sepodi.hearing.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.workflow.module.sepodi.hearing.api.StopPointWorkflowApiV1;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.module.sepodi.hearing.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.module.sepodi.hearing.service.DecisionService;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowService;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowTransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StopPointWorkflowApiV1Controller implements StopPointWorkflowApiV1 {

  private final StopPointWorkflowService service;
  private final DecisionService decisionService;
  private final StopPointWorkflowTransitionService workflowTransitionService;

  @Override
  public ReadStopPointWorkflowModel getStopPointWorkflow(Long id) {
    ReadStopPointWorkflowModel stopPointWorkflowModel = StopPointWorkflowMapper.toModel(service.getWorkflow(id));

    service.getWorkflowByFollowUpId(id).ifPresent(stopPointWorkflow ->
        stopPointWorkflowModel.setPreviousWorkflowId(stopPointWorkflow.getId())
    );

    decisionService.addJudgementsToExaminants(stopPointWorkflowModel.getExaminants());
    return stopPointWorkflowModel;
  }

  @Override
  public Container<ReadStopPointWorkflowModel> getStopPointWorkflows(Pageable pageable,
      StopPointWorkflowRequestParams stopPointWorkflowRequestParams) {
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

}
