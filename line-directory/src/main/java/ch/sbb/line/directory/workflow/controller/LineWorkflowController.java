package ch.sbb.line.directory.workflow.controller;

import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.line.directory.workflow.api.LineWorkflowEvent;
import ch.sbb.line.directory.workflow.api.LineWorkflowApi;
import ch.sbb.line.directory.workflow.service.LineWorkflowProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LineWorkflowController implements LineWorkflowApi {

  private final LineWorkflowProcessingService lineWorkflowProcessingService;

  @Override
  public WorkflowStatus processWorkflow(LineWorkflowEvent workflowEvent) {
    return lineWorkflowProcessingService.processLineWorkflow(workflowEvent);
  }
}
