package ch.sbb.workflow.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Workflow")
@RequestMapping("v1/workflows")
public interface WorkflowApiV1 {

  @GetMapping("{id}")
  WorkflowModel getWorkflow(@PathVariable Long id);

  @GetMapping()
  List<WorkflowModel> getWorkflows();

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  WorkflowModel startWorkflow(@RequestBody @Valid WorkflowStartModel workflowStartModel);

  @PreAuthorize("@userAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  @PostMapping("{id}/examinant-check")
  WorkflowModel examinantCheck(@PathVariable Long id, @RequestBody @Valid ExaminantWorkflowCheckModel examinantWorkflowCheckModel);

}
