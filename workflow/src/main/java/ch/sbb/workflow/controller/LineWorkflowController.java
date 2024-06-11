package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.api.workflow.WorkflowModel;
import ch.sbb.atlas.api.workflow.WorkflowStartModel;
import ch.sbb.workflow.api.LineWorkflowApiV1;
import ch.sbb.workflow.entity.LineWorkflow;
import ch.sbb.workflow.mapper.LineWorkflowMapper;
import ch.sbb.workflow.mapper.WorkflowStartMapper;
import ch.sbb.workflow.service.lidi.LineWorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineWorkflowController implements LineWorkflowApiV1 {

  private final LineWorkflowService service;

  @Override
  public WorkflowModel getWorkflow(Long id) {
    return LineWorkflowMapper.toModel(service.getWorkflow(id));
  }

  @Override
  public List<WorkflowModel> getWorkflows() {
    return service.getWorkflows().stream().map(LineWorkflowMapper::toModel).toList();
  }

  @Override
  public WorkflowModel startWorkflow(WorkflowStartModel workflowStartModel) {
    log.info("Starting workflow");
    LineWorkflow lineWorkflow = service.startWorkflow(WorkflowStartMapper.toEntity(workflowStartModel));
    return LineWorkflowMapper.toNewModel(lineWorkflow);
  }

  @Override
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public WorkflowModel examinantCheck(Long id, ExaminantWorkflowCheckModel examinantWorkflowCheckModel) {
    log.info("Checking workflow");
    LineWorkflow lineWorkflow = service.examinantCheck(id, examinantWorkflowCheckModel);
    return LineWorkflowMapper.toModel(lineWorkflow);
  }

}
