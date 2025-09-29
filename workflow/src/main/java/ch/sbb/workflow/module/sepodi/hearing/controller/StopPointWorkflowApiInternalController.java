package ch.sbb.workflow.module.sepodi.hearing.controller;

import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.sepodi.hearing.api.StopPointWorkflowApiInternal;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.module.sepodi.hearing.mapper.StopPointWorkflowDecisionMapper;
import ch.sbb.workflow.module.sepodi.hearing.mapper.StopPointWorkflowMapper;
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
import ch.sbb.workflow.module.sepodi.hearing.service.DecisionService;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowEndExpiredService;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowOtpService;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowService;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowTransitionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StopPointWorkflowApiInternalController implements StopPointWorkflowApiInternal {

  private final StopPointWorkflowService service;
  private final StopPointWorkflowOtpService otpService;
  private final DecisionService decisionService;
  private final StopPointWorkflowTransitionService workflowTransitionService;
  private final StopPointWorkflowEndExpiredService endExpiredWorkflowsService;

  @Override
  public List<StopPointClientPersonModel> getExaminants(Long servicePointVersionId) {
    return service.getExaminantsByServicePointVersionId(servicePointVersionId);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public ReadStopPointWorkflowModel startStopPointWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.startWorkflow(id));
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public ReadStopPointWorkflowModel rejectStopPointWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.rejectWorkflow(id, workflowModel));
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public ReadStopPointWorkflowModel editStopPointWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(service.editWorkflow(id, workflowModel));
  }

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService."
      + "isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public void addExaminantsToStopPointWorkflow(Long id, AddExaminantsModel addExaminantsModel) {
    service.addExaminants(id, addExaminantsModel);
  }

  @Override
  public void obtainOtp(Long id, OtpRequestModel otpRequest) {
    otpService.obtainOtp(service.findStopPointWorkflow(id), otpRequest.getExaminantMail());
  }

  @Override
  public StopPointClientPersonModel verifyOtp(Long id, OtpVerificationModel otpVerification) {
    Person examinant = otpService.verifyExaminantPinCode(id, otpVerification);
    return StopPointClientPersonMapper.toModel(examinant);
  }

  @Override
  public ReadDecisionModel getDecision(Long personId) {
    StopPointWorkflow stopPointWorkflow = service.findStopPointWorkflowByPersonId(personId);
    return StopPointWorkflowDecisionMapper.toModel(
        decisionService.getDecisionByExaminantId(personId, stopPointWorkflow.getSboid()));
  }

  @Override
  public void voteWorkflow(Long id, Long personId, DecisionModel decisionModel) {
    otpService.verifyExaminantPinCode(id, decisionModel);

    service.voteWorkFlow(id, personId, decisionModel);
    workflowTransitionService.progressWorkflowWithNewDecision(id);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public void overrideVoteWorkflow(Long id, Long personId, OverrideDecisionModel decisionModel) {
    service.overrideVoteWorkflow(id, personId, decisionModel);
    workflowTransitionService.progressWorkflowWithNewDecision(id);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public ReadStopPointWorkflowModel restartStopPointWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.restartWorkflow(id, restartWorkflowModel));
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  @Override
  public ReadStopPointWorkflowModel cancelStopPointWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.cancelWorkflow(id, stopPointCancelWorkflowModel));
  }

  @Override
  public void endExpiredWorkflows() {
    endExpiredWorkflowsService.endExpiredWorkflows();
  }

}
