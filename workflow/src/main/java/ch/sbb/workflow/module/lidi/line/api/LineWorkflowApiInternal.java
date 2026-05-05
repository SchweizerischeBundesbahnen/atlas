package ch.sbb.workflow.module.lidi.line.api;

import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.api.workflow.WorkflowModel;
import ch.sbb.atlas.api.workflow.WorkflowStartModel;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Line Workflow")
@RequestMapping(LineWorkflowApiInternal.BASE_PATH)
public interface LineWorkflowApiInternal {

  String BASE_PATH = "/internal/line/workflows";

  @UnauthorizedAllowed(limitations = FurtherLimitations.REDACTED)
  @GetMapping("{id}")
  WorkflowModel getWorkflow(@PathVariable Long id);

  @UnauthorizedAllowed(limitations = FurtherLimitations.REDACTED)
  @GetMapping()
  List<WorkflowModel> getWorkflows();

  // Actual permission check on LineWorkflowProcessingService.processLineWorkflow
  @AuthorizedOnly
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201")})
  WorkflowModel startWorkflow(@RequestBody @Valid WorkflowStartModel workflowStartModel);

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  @PostMapping("{id}/examinant-check")
  WorkflowModel examinantCheck(@PathVariable Long id,
      @RequestBody @Valid ExaminantWorkflowCheckModel examinantWorkflowCheckModel);

}
