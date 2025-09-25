package ch.sbb.workflow.module.sepodi.hearing.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointWorkflowRequestParams;
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

@Tag(name = "StopPointWorkflow")
@RequestMapping("v1/stop-point/workflows")
public interface StopPointWorkflowApiV1 {

  @GetMapping("{id}")
  ReadStopPointWorkflowModel getStopPointWorkflow(@PathVariable Long id);

  @GetMapping
  @PageableAsQueryParam
  Container<ReadStopPointWorkflowModel> getStopPointWorkflows(
      @Parameter(hidden = true) @PageableDefault(sort = {StopPointWorkflow.Fields.sloid,
          StopPointWorkflow.Fields.versionValidFrom}) Pageable pageable,
      @ParameterObject StopPointWorkflowRequestParams stopPointWorkflowRequestParams);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  ReadStopPointWorkflowModel addStopPointWorkflow(@RequestBody @Valid StopPointAddWorkflowModel workflowModel);

}
