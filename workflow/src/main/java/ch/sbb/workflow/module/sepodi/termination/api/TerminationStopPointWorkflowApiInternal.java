package ch.sbb.workflow.module.sepodi.termination.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationInfoModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "TerminationStopPointWorkflow")
@RequestMapping(TerminationStopPointWorkflowApiInternal.BASE_PATH)
public interface TerminationStopPointWorkflowApiInternal {

  String BASE_PATH = "/internal/termination-stop-point/workflows";

  @GetMapping("/termination-info/{sloid}")
  TerminationInfoModel getTerminationInfoBySloid(@PathVariable String sloid);

  @PreAuthorize(
      "@servicePointTerminationBasedUserAdministrationService.hasUserInfoPlusTerminationVotePermission()")
  @PostMapping(path = "/decision/info-plus/{workflowId}")
  @ResponseStatus(HttpStatus.CREATED)
  TerminationStopPointWorkflowModel decisionInfoPlus(@RequestBody @Valid TerminationDecisionModel decisionModel,
      @PathVariable Long workflowId);

  @PreAuthorize(
      "@servicePointTerminationBasedUserAdministrationService.hasUserNovaTerminationVotePermission()")
  @PostMapping(path = "/decision/nova/{workflowId}")
  @ResponseStatus(HttpStatus.CREATED)
  TerminationStopPointWorkflowModel decisionNova(@RequestBody @Valid TerminationDecisionModel decisionModel,
      @PathVariable Long workflowId);

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PostMapping(path = "/abort/{workflowId}")
  @ResponseStatus(HttpStatus.OK)
  TerminationStopPointWorkflowModel abortTermination(@RequestBody @Valid TerminationAbortModel abortModel,
      @PathVariable Long workflowId);
}
