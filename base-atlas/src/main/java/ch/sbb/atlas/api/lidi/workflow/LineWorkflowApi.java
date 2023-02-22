package ch.sbb.atlas.api.lidi.workflow;

import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Hidden
public interface LineWorkflowApi {

  String BASEPATH = "v1/lines/workflow/";

  @PostMapping(BASEPATH + "process")
  WorkflowStatus processWorkflow(@Valid @RequestBody WorkflowEvent workflowEvent);

}
