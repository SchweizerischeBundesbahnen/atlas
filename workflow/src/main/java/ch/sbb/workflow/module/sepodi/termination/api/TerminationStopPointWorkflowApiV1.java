package ch.sbb.workflow.module.sepodi.termination.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowFilterParams;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "TerminationStopPointWorkflow")
@RequestMapping("v1/termination-stop-point/workflows")
public interface TerminationStopPointWorkflowApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<TerminationStopPointWorkflowModel> getTerminationStopPointWorkflows(
      @Parameter(hidden = true) @PageableDefault(sort = {TerminationStopPointWorkflow.Fields.id}) Pageable pageable,
      @ParameterObject TerminationStopPointWorkflowFilterParams filterParams
  );

  @GetMapping("{id}")
  TerminationStopPointWorkflowModel getTerminationStopPointWorkflow(@PathVariable Long id);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  TerminationStopPointWorkflowModel startTerminationStopPointWorkflow(
      @RequestBody @Valid StartTerminationStopPointWorkflowModel workflowModel);

}
