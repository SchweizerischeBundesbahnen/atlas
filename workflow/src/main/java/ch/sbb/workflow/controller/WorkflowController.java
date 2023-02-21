package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.api.workflow.WorkflowApiV1;
import ch.sbb.atlas.api.workflow.WorkflowModel;
import ch.sbb.atlas.api.workflow.WorkflowStartModel;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.mapper.WorkflowMapper;
import ch.sbb.workflow.mapper.WorkflowStartMapper;
import ch.sbb.workflow.service.WorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WorkflowController implements WorkflowApiV1 {

  private final WorkflowService service;

  @Override
  public WorkflowModel getWorkflow(Long id) {
    return WorkflowMapper.toModel(service.getWorkflow(id));
  }

  @Override
  public List<WorkflowModel> getWorkflows() {
    return service.getWorkflows().stream().map(WorkflowMapper::toModel).toList();
  }

  @Override
  public WorkflowModel startWorkflow(WorkflowStartModel workflowStartModel) {
    log.info("Starting workflow");
    Workflow workflow = service.startWorkflow(WorkflowStartMapper.toEntity(workflowStartModel));
    return WorkflowMapper.toNewModel(workflow);
  }

  @Override
  @PreAuthorize("@userAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public WorkflowModel examinantCheck(Long id, ExaminantWorkflowCheckModel examinantWorkflowCheckModel) {
    log.info("Checking workflow");
    Workflow workflow = service.examinantCheck(id, examinantWorkflowCheckModel);
    return WorkflowMapper.toModel(workflow);
  }

}
