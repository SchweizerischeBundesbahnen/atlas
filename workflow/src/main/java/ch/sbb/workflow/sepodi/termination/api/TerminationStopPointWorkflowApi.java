package ch.sbb.workflow.sepodi.termination.api;

import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "TerminationStopPointWorkflow")
@RequestMapping("internal/termination-stop-point/workflows")
public interface TerminationStopPointWorkflowApi {

  @GetMapping("{id}")
  TerminationStopPointWorkflowModel getTerminationStopPointWorkflow(@PathVariable Long id);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  TerminationStopPointWorkflowModel startTerminationStopPointWorkflow(
      @RequestBody @Valid StartTerminationStopPointWorkflowModel workflowModel);

}
