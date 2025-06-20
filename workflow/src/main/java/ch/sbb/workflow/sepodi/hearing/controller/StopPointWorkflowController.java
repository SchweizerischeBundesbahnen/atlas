package ch.sbb.workflow.sepodi.hearing.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.api.StopPointWorkflowApiV1;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.sepodi.hearing.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.sepodi.hearing.mapper.StopPointWorkflowDecisionMapper;
import ch.sbb.workflow.sepodi.hearing.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.hearing.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.AddExaminantsModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.DecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.ReadDecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.sepodi.hearing.service.DecisionService;
import ch.sbb.workflow.sepodi.hearing.service.StopPointWorkflowEndExpiredService;
import ch.sbb.workflow.sepodi.hearing.service.StopPointWorkflowOtpService;
import ch.sbb.workflow.sepodi.hearing.service.StopPointWorkflowService;
import ch.sbb.workflow.sepodi.hearing.service.StopPointWorkflowTransitionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StopPointWorkflowController implements StopPointWorkflowApiV1 {

  private final StopPointWorkflowService service;
  private final StopPointWorkflowOtpService otpService;
  private final DecisionService decisionService;
  private final StopPointWorkflowTransitionService workflowTransitionService;
  private final StopPointWorkflowEndExpiredService endExpiredWorkflowsService;

  @Override
  public List<StopPointClientPersonModel> getExaminants(Long servicePointVersionId) {
    return service.getExaminantsByServicePointVersionId(servicePointVersionId);
  }

  @Override
  public ReadStopPointWorkflowModel getStopPointWorkflow(Long id) {
    ReadStopPointWorkflowModel stopPointWorkflowModel = StopPointWorkflowMapper.toModel(service.getWorkflow(id));

    service.getWorkflowByFollowUpId(id).ifPresent(stopPointWorkflow ->
        stopPointWorkflowModel.setPreviousWorkflowId(stopPointWorkflow.getId())
    );

    decisionService.addJudgementsToExaminants(stopPointWorkflowModel.getExaminants());
    return stopPointWorkflowModel;
  }

  @Override
  public Container<ReadStopPointWorkflowModel> getStopPointWorkflows(Pageable pageable,
      StopPointWorkflowRequestParams stopPointWorkflowRequestParams) {
    StopPointWorkflowSearchRestrictions stopPointWorkflowSearchRestrictions = StopPointWorkflowSearchRestrictions.builder()
        .pageable(pageable)
        .stopPointWorkflowRequestParams(stopPointWorkflowRequestParams)
        .build();
    Page<StopPointWorkflow> workflows = service.getWorkflows(stopPointWorkflowSearchRestrictions);

    return Container.<ReadStopPointWorkflowModel>builder()
        .objects(workflows.stream().map(StopPointWorkflowMapper::toModel).toList())
        .totalCount(workflows.getTotalElements())
        .build();
  }

  @Override
  public ReadStopPointWorkflowModel addStopPointWorkflow(StopPointAddWorkflowModel workflowModel) {
    return StopPointWorkflowMapper.toModel(workflowTransitionService.addWorkflow(workflowModel));
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
