package ch.sbb.workflow.api;

import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Workflow")
@RequestMapping("v1/stop-point/workflows")
public interface StopPointWorkflowApiV1 {

  @GetMapping("{id}")
  StopPointAddWorkflowModel getWorkflow(@PathVariable Long id);

  @GetMapping()
  List<StopPointAddWorkflowModel> getWorkflows();

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  StopPointAddWorkflowModel addWorkflow(@RequestBody @Valid StopPointAddWorkflowModel workflowStartModel);

  @PutMapping(path = "{id}")
  StopPointAddWorkflowModel startWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointAddWorkflowModel workflowStartModel);

}
