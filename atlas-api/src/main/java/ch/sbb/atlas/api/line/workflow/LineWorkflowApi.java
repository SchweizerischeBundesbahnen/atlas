package ch.sbb.atlas.api.line.workflow;

import ch.sbb.atlas.base.service.model.workflow.WorkflowEvent;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import io.swagger.v3.oas.annotations.Hidden;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Hidden
public interface LineWorkflowApi {

  String BASEPATH = "v1/lines/workflow/";

  @PostMapping(BASEPATH + "process")
  WorkflowStatus processWorkflow(@Valid @RequestBody WorkflowEvent workflowEvent);

}
