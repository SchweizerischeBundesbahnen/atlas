package ch.sbb.line.directory.workflow.controller;

import ch.sbb.atlas.api.lidi.workflow.LineWorkflowApi;
import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.workflow.service.LineWorkflowProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LineWorkflowController implements LineWorkflowApi {

  private final LineWorkflowProcessingService lineWorkflowProcessingService;

  @Override
  public WorkflowStatus processWorkflow(WorkflowEvent workflowEvent) {
    return lineWorkflowProcessingService.processLineWorkflow(workflowEvent, lineWorkflowProcessingService.getObjectVersion(workflowEvent));
  }
}
