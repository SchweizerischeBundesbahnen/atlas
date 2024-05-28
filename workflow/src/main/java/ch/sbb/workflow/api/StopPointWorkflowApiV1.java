package ch.sbb.workflow.api;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.DecisionModel;
import ch.sbb.atlas.api.workflow.OverrideDecisionModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.api.workflow.StopPointRejectWorkflowModel;
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

@Tag(name = "StopPointWorkflow")
@RequestMapping("v1/stop-point/workflows")
public interface StopPointWorkflowApiV1 {

  @GetMapping("{id}")
  StopPointAddWorkflowModel getStopPointWorkflow(@PathVariable Long id);

  @GetMapping()
  List<StopPointAddWorkflowModel> getStopPointWorkflows();

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  StopPointAddWorkflowModel addStopPointWorkflow(@RequestBody @Valid StopPointAddWorkflowModel workflowModel);

  @PutMapping(path = "/start/{id}")
  StopPointAddWorkflowModel startStopPointWorkflow(@PathVariable Long id);

  @PutMapping(path = "/edit/{id}")
  StopPointAddWorkflowModel editStopPointWorkflow(@PathVariable Long id, @RequestBody @Valid StopPointAddWorkflowModel workflowModel);

  @PutMapping(path = "/reject/{id}")
  StopPointAddWorkflowModel rejectStopPointWorkflow(@PathVariable Long id, @RequestBody @Valid StopPointRejectWorkflowModel workflowModel);

  @PutMapping(path = "/add-examinant/{id}")
  StopPointAddWorkflowModel addExaminantToStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid ClientPersonModel personModel);

  @PutMapping(path = "/remove-examinant/{id}/{personId}")
  StopPointAddWorkflowModel removeExaminantFromStopPointWorkflow(@PathVariable Long id,@PathVariable Long personId);

  @PutMapping(path = "/obtain-otp/{id}/{personId}")
  void obtainOtpForStopPointWorkflow(@PathVariable Long id,@PathVariable Long personId);

  @PostMapping(path = "/vote/{id}/{personId}")
  void voteWorkflow(@PathVariable Long id, @PathVariable Long personId, @RequestBody @Valid DecisionModel decisionModel);
  @PostMapping(path = "/override-vote/{id}/{personId}")
  void overrideVoteWorkflow(@PathVariable Long id, @PathVariable Long personId, @RequestBody @Valid OverrideDecisionModel decisionModel);

  //TODO: 1. restartWorkflow(id, designationOfficial, decisionComment, ClientPersonModel)
  //TODO: 2. rejectWorkflow(id, decisionComment, ClientPersonModel)
  //TODO: 3. overrideVoteWorkflow(id, ClientPersonModel, Decision)

}
