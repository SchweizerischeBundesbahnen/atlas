package ch.sbb.workflow.controller;

import ch.sbb.workflow.api.WorkflowApiV1;
import ch.sbb.workflow.api.WorkflowModel;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.service.WorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WorkflowController implements WorkflowApiV1 {

  private final WorkflowService service;

  @Override
  public WorkflowModel getWorkflow(Long id) {
    return WorkflowModel.toModel(service.getWorkflow(id));
  }

  @Override
  public List<WorkflowModel> getWorkflows() {
    return service.getWorkflows().stream().map(WorkflowModel::toModel).toList();
  }

  @Override
  public WorkflowModel startWorkflow(WorkflowModel newWorkflow) {
    Workflow workflow = service.startWorkflow(WorkflowModel.toEntity(newWorkflow));
    return WorkflowModel.toModel(workflow);
  }
}
