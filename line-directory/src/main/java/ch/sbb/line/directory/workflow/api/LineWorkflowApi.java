package ch.sbb.line.directory.workflow.api;

import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.PostMapping;

@Hidden
public interface LineWorkflowApi {

  String BASEPATH = "v1/lines/workflow/";

  @PostMapping(BASEPATH + "process")
  WorkflowStatus processWorkflow(LineWorkflowEvent workflowEvent);

}
