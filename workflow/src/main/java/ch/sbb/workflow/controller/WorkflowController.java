package ch.sbb.workflow.controller;

import ch.sbb.workflow.api.WorkflowApiV1;
import ch.sbb.workflow.api.ExaminantWorkflowCheckModel;
import ch.sbb.workflow.api.WorkflowModel;
import ch.sbb.workflow.api.WorkflowStartModel;
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
  public WorkflowModel startWorkflow(WorkflowStartModel workflowStartModel) {
    Workflow workflow = service.startWorkflow(WorkflowStartModel.toEntity(workflowStartModel));
    return WorkflowModel.toNewModel(workflow);
  }

  @Override
  public WorkflowModel examinantCheck(Long id, ExaminantWorkflowCheckModel examinantWorkflowCheckModel) {
    Workflow workflow = service.examinantCheck(service.getWorkflow(id), examinantWorkflowCheckModel);
    return WorkflowModel.toNewModel(workflow);
  }

}
