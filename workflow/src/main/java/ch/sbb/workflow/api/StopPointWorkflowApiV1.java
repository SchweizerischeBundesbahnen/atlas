package ch.sbb.workflow.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
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

  @PostMapping(path = "/start/{id}")
  ReadStopPointWorkflowModel startStopPointWorkflow(@PathVariable Long id);

  @PostMapping(path = "/edit/{id}")
  ReadStopPointWorkflowModel editStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid EditStopPointWorkflowModel workflowModel);

  @PostMapping(path = "/reject/{id}")
  ReadStopPointWorkflowModel rejectStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointRejectWorkflowModel workflowModel);

  @PostMapping(path = "/add-examinant/{id}")
  ReadStopPointWorkflowModel addExaminantToStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointClientPersonModel personModel);

  @PostMapping(path = "/remove-examinant/{id}/{personId}")
  ReadStopPointWorkflowModel removeExaminantFromStopPointWorkflow(@PathVariable Long id, @PathVariable Long personId);

  @PostMapping(path = "/obtain-otp/{id}/{personId}")
  void obtainOtpForStopPointWorkflow(@PathVariable Long id, @PathVariable Long personId);

  @PostMapping(path = "/vote/{id}/{personId}")
  void voteWorkflow(@PathVariable Long id, @PathVariable Long personId, @RequestBody @Valid DecisionModel decisionModel);

  @PostMapping(path = "/override-vote/{id}/{personId}")
  void overrideVoteWorkflow(@PathVariable Long id, @PathVariable Long personId,
      @RequestBody @Valid OverrideDecisionModel decisionModel);

  @PostMapping(path = "/restart/{id}")
  ReadStopPointWorkflowModel restartStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointRestartWorkflowModel restartWorkflowModel);

  @PostMapping(path = "/cancel/{id}")
  ReadStopPointWorkflowModel cancelStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointRejectWorkflowModel stopPointCancelWorkflowModel);

}
