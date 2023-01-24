package ch.sbb.line.directory.workflow.api;

import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Hidden
@RequestMapping("v1/lines/workflow")
public interface LineWorkflowApi {

  @PostMapping("start")
  WorkflowStatus startWorkflow(LineWorkflowEvent workflowEvent);

}
