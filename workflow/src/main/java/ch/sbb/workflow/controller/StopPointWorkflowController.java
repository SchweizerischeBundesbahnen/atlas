package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
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
  public StopPointAddWorkflowModel addWorkflow(StopPointAddWorkflowModel workflowStartModel) {
    return StopPointWorkflowMapper.toModel(service.addWorkflow(workflowStartModel));
  }

  @Override
  public StopPointAddWorkflowModel startWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.startWorkflow(id));
  }

}
