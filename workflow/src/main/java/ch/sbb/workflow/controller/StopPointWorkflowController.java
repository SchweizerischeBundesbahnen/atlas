package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.api.workflow.StopPointRejectWorkflowModel;
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
  public StopPointAddWorkflowModel getWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.getWorkflow(id));
  }

  @Override
  public List<StopPointAddWorkflowModel> getWorkflows() {
    return service.getWorkflows().stream().map(StopPointWorkflowMapper::toModel).toList();
  }

  @Override
  public StopPointAddWorkflowModel addWorkflow(StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.addWorkflow(workflowModel));
  }

  @Override
  public StopPointAddWorkflowModel startWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.startWorkflow(id));
  }

  @Override
  public StopPointAddWorkflowModel editWorkflow(Long id, StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.editWorkflow(id,workflowModel));
  }

  @Override
  public StopPointAddWorkflowModel rejectWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.rejectWorkflow(id,workflowModel));
  }

  @Override
  public StopPointAddWorkflowModel addExaminantToWorkflow(Long id, ClientPersonModel personModel) {
    return StopPointWorkflowMapper.toModel(service.addExaminantToWorkflow(id,personModel));
  }

  @Override
  public StopPointAddWorkflowModel removeExaminantToWorkflow(Long id, Long personId) {
    return StopPointWorkflowMapper.toModel(service.removeExaminantToWorkflow(id,personId));
  }

}
