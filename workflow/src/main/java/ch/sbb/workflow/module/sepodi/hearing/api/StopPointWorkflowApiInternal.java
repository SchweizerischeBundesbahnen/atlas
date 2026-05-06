package ch.sbb.workflow.module.sepodi.hearing.api;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.AddExaminantsModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.DecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.ReadDecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointRestartWorkflowModel;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "StopPointWorkflow")
@RequestMapping(StopPointWorkflowApiInternal.BASE_PATH)
public interface StopPointWorkflowApiInternal {

  String BASE_PATH = "/internal/stop-point/workflows";

  @UnauthorizedAllowed(limitations = FurtherLimitations.REDACTED)
  @GetMapping("{servicePointVersionId}/examinants")
  List<StopPointClientPersonModel> getExaminants(@PathVariable Long servicePointVersionId);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/start/{id}")
  ReadStopPointWorkflowModel startStopPointWorkflow(@PathVariable Long id);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/edit/{id}")
  ReadStopPointWorkflowModel editStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid EditStopPointWorkflowModel workflowModel);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/add-examinants/{id}")
  void addExaminantsToStopPointWorkflow(@PathVariable Long id, @RequestBody @Valid AddExaminantsModel addExaminantsModel);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/reject/{id}")
  ReadStopPointWorkflowModel rejectStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointRejectWorkflowModel workflowModel);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  @ApiResponses(value = {@ApiResponse(responseCode = "202")})
  @PostMapping(path = "/obtain-otp/{id}")
  void obtainOtp(@PathVariable Long id, @RequestBody @Valid OtpRequestModel otpRequest);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @PostMapping(path = "/verify-otp/{id}")
  StopPointClientPersonModel verifyOtp(@PathVariable Long id, @RequestBody @Valid OtpVerificationModel otpVerification);

  @UnauthorizedAllowed(limitations = FurtherLimitations.REDACTED)
  @GetMapping(path = "/decisions/{personId}")
  ReadDecisionModel getDecision(@PathVariable Long personId);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @PostMapping(path = "/vote/{id}/{personId}")
  void voteWorkflow(@PathVariable Long id, @PathVariable Long personId, @RequestBody @Valid DecisionModel decisionModel);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/override-vote/{id}/{personId}")
  void overrideVoteWorkflow(@PathVariable Long id, @PathVariable Long personId,
      @RequestBody @Valid OverrideDecisionModel decisionModel);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/restart/{id}")
  ReadStopPointWorkflowModel restartStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointRestartWorkflowModel restartWorkflowModel);

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @PostMapping(path = "/cancel/{id}")
  ReadStopPointWorkflowModel cancelStopPointWorkflow(@PathVariable Long id,
      @RequestBody @Valid StopPointRejectWorkflowModel stopPointCancelWorkflowModel);

  @PostMapping(path = "/end-expired")
  @AdminOnly
  @Operation(description = "End all expired workflow")
  void endExpiredWorkflows();

}
