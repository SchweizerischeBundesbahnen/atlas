package ch.sbb.workflow.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Lines")
@RequestMapping("v1/workflows")
public interface WorkflowApiV1 {

  @GetMapping("{id}")
  WorkflowModel getWorkflows(@PathVariable Long id);

  @GetMapping()
  List<WorkflowModel> getWorkflows();

  @PostMapping
  WorkflowModel createWorkflow(@RequestBody @Valid WorkflowModel newWorkflow);
}
