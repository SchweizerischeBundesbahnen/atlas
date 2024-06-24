package ch.sbb.workflow.service.sepodi;

import static ch.sbb.atlas.workflow.model.WorkflowStatus.REJECTED;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.mapper.ClientPersonMapper;
import ch.sbb.workflow.mapper.PersonMapper;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.sepodi.Examinants;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowTransitionService {

  private static final String EXCEPTION_HEARING_MSG = "Workflow status must be HEARING!!!(REPLACE ME WITH A CUSTOM EXCEPTION)";
  private static final int WORKFLOW_DURATION_IN_DAYS = 31;

  private final DecisionService decisionService;
  private final SePoDiClientService sePoDiClientService;
  private final Examinants examinants;
  private final StopPointWorkflowNotificationService notificationService;
  private final StopPointWorkflowService stopPointWorkflowService;

  /**
   * Authorization for this method is delegated to ServicePointService#update()
   */
  public StopPointWorkflow addWorkflow(StopPointAddWorkflowModel stopPointAddWorkflowModel) {
    stopPointWorkflowService.checkHasWorkflowAdded(stopPointAddWorkflowModel.getVersionId());
    ReadServicePointVersionModel servicePointVersionModel = sePoDiClientService.updateStopPointStatusToInReview(
        stopPointAddWorkflowModel.getSloid(), stopPointAddWorkflowModel.getVersionId());
    StopPointWorkflow stopPointWorkflow = createStopPointAddWorkflow(stopPointAddWorkflowModel, servicePointVersionModel);
    stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
    return stopPointWorkflowService.save(stopPointWorkflow);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public StopPointWorkflow startWorkflow(Long id) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(),
        WorkflowStatus.HEARING);
    stopPointWorkflow.setStatus(WorkflowStatus.HEARING);
    stopPointWorkflow.setEndDate(LocalDate.now().plusDays(WORKFLOW_DURATION_IN_DAYS));
    StopPointWorkflow workflow = stopPointWorkflowService.save(stopPointWorkflow);
    notificationService.sendStartStopPointWorkflowMail(workflow);
    return workflow;
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public StopPointWorkflow rejectWorkflow(Long id, StopPointRejectWorkflowModel rejectWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(), REJECTED);
    Person examinantBAV = PersonMapper.toPersonEntity(rejectWorkflowModel);
    decisionService.createRejectedDecision(examinantBAV, rejectWorkflowModel.getMotivationComment());
    sePoDiClientService.updateStoPointStatusToDraft(stopPointWorkflow);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    stopPointWorkflow.setStatus(REJECTED);
    StopPointWorkflow workflow = stopPointWorkflowService.save(stopPointWorkflow);
    notificationService.sendRejectStopPointWorkflowMail(workflow);
    return stopPointWorkflow;
  }

  public StopPointWorkflow cancelWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException(EXCEPTION_HEARING_MSG);
    }
    Person examinantBAV = PersonMapper.toPersonEntity(stopPointCancelWorkflowModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    Decision decision = new Decision();
    decision.setJudgement(JudgementType.NO);
    decision.setDecisionType(DecisionType.CANCELED);
    decision.setExaminant(examinantBAV);
    decision.setMotivation(stopPointCancelWorkflowModel.getMotivationComment());
    decision.setMotivationDate(LocalDateTime.now());
    decisionService.save(decision);

    stopPointWorkflow.setStatus(WorkflowStatus.CANCELED);
    return stopPointWorkflow;
  }

  public StopPointWorkflow restartWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException(EXCEPTION_HEARING_MSG);
    }
    //TODO String newDesignationOfficial = restartWorkflowModel.getNewDesignationOfficial();
    // sePoDiClient.update(officialDesignation)

    ClientPersonModel examinantBAVclientPersonModel = restartWorkflowModel.getExaminantBAVClient();
    Person examinantBAV = ClientPersonMapper.toEntity(examinantBAVclientPersonModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    Decision decision = new Decision();
    decision.setDecisionType(DecisionType.RESTARTED);
    decision.setExaminant(examinantBAV);
    decision.setMotivation(restartWorkflowModel.getMotivationComment());
    decision.setMotivationDate(LocalDateTime.now());
    decisionService.save(decision);

    //create new Workflow
    StopPointWorkflow newStopPointWorkflow = StopPointWorkflow.builder()
        .workflowComment(restartWorkflowModel.getMotivationComment())
        .designationOfficial(restartWorkflowModel.getNewDesignationOfficial())
        .status(WorkflowStatus.ADDED)
        .examinants(new HashSet<>(stopPointWorkflow.getExaminants()))
        .ccEmails(new ArrayList<>(stopPointWorkflow.getCcEmails()))
        .sboid(stopPointWorkflow.getSboid())
        .versionId(stopPointWorkflow.getVersionId())
        .sloid(stopPointWorkflow.getSloid())
        .localityName(stopPointWorkflow.getLocalityName())
        .startDate(stopPointWorkflow.getStartDate())//todo
        .endDate(stopPointWorkflow.getEndDate())
        .build();
    stopPointWorkflowService.save(newStopPointWorkflow);
    //update current workflow
    stopPointWorkflow.setStatus(REJECTED);
    stopPointWorkflow.setFollowUpWorkflow(newStopPointWorkflow);
    stopPointWorkflowService.save(stopPointWorkflow);
    return newStopPointWorkflow;
  }

  private StopPointWorkflow createStopPointAddWorkflow(StopPointAddWorkflowModel workflowStartModel,
      ReadServicePointVersionModel servicePointVersionModel) {
    SwissCanton swissCanton = servicePointVersionModel.getServicePointGeolocation().getSwissLocation().getCanton();
    List<StopPointClientPersonModel> personModels = examinants.getExaminants(swissCanton);
    return StopPointWorkflowMapper.addStopPointWorkflowToEntity(workflowStartModel, servicePointVersionModel, personModels);
  }

}
