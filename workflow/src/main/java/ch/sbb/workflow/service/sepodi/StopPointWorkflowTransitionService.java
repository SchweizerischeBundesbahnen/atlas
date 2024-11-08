package ch.sbb.workflow.service.sepodi;

import static ch.sbb.atlas.workflow.model.WorkflowStatus.REJECTED;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.aop.LoggingAspect;
import ch.sbb.workflow.aop.MethodLogged;
import ch.sbb.workflow.client.SePoDiAdminClient;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.mapper.PersonMapper;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.sepodi.Examinants;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowTransitionService {

  private final DecisionService decisionService;
  private final SePoDiClientService sePoDiClientService;
  private final SePoDiClient sePoDiClient;
  private final SePoDiAdminClient sePoDiAdminClient;
  private final Examinants examinants;
  private final StopPointWorkflowNotificationService notificationService;
  private final StopPointWorkflowService stopPointWorkflowService;

  static final int WORKFLOW_DURATION_IN_DAYS = 31;

  /**
   * Authorization for this method is delegated to ServicePointService#update()
   */
  @MethodLogged(workflowType = LoggingAspect.ADD_WORKFLOW)
  public StopPointWorkflow addWorkflow(StopPointAddWorkflowModel stopPointAddWorkflowModel) {
    stopPointWorkflowService.checkHasWorkflowAdded(stopPointAddWorkflowModel.getVersionId());
    if (stopPointAddWorkflowModel.getExaminants() != null && !stopPointAddWorkflowModel.getExaminants().isEmpty()) {
      stopPointWorkflowService.checkIfAllExaminantEmailsAreUnique(stopPointAddWorkflowModel.getExaminants(), true);
    }
    ReadServicePointVersionModel servicePointVersionModel = sePoDiClientService.updateStopPointStatusToInReview(sePoDiClient,
        stopPointAddWorkflowModel.getSloid(), stopPointAddWorkflowModel.getVersionId());
    StopPointWorkflow stopPointWorkflow = createStopPointAddWorkflow(stopPointAddWorkflowModel, servicePointVersionModel);
    stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
    return stopPointWorkflowService.save(stopPointWorkflow);
  }

  public StopPointWorkflow startWorkflow(Long id) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(),
        WorkflowStatus.HEARING);

    stopPointWorkflow.setStatus(WorkflowStatus.HEARING);
    stopPointWorkflow.setStartDate(LocalDate.now());
    stopPointWorkflow.setEndDate(LocalDate.now().plusDays(WORKFLOW_DURATION_IN_DAYS));
    StopPointWorkflow workflow = stopPointWorkflowService.save(stopPointWorkflow);
    notificationService.sendStartStopPointWorkflowMail(workflow);
    return workflow;
  }

  @MethodLogged(workflowType = LoggingAspect.REJECT_WORKFLOW)
  public StopPointWorkflow rejectWorkflow(Long id, StopPointRejectWorkflowModel rejectWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(), REJECTED);

    Person examinantBAV = PersonMapper.toPersonEntity(rejectWorkflowModel);
    decisionService.createRejectedDecision(examinantBAV, rejectWorkflowModel.getMotivationComment());
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    stopPointWorkflow.setStatus(REJECTED);
    StopPointWorkflow workflow = stopPointWorkflowService.save(stopPointWorkflow);
    sePoDiClientService.updateStopPointStatusToDraft(sePoDiClient, stopPointWorkflow);
    notificationService.sendRejectStopPointWorkflowMail(workflow, rejectWorkflowModel.getMotivationComment());
    return stopPointWorkflow;
  }

  @MethodLogged(workflowType = LoggingAspect.CANCEL_WORKFLOW)
  public StopPointWorkflow cancelWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(),
        WorkflowStatus.CANCELED);

    Person examinantBAV = PersonMapper.toPersonEntity(stopPointCancelWorkflowModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    decisionService.createCanceledDecision(examinantBAV, stopPointCancelWorkflowModel.getMotivationComment());

    stopPointWorkflow.setEndDate(LocalDate.now());
    stopPointWorkflow.setStatus(WorkflowStatus.CANCELED);
    StopPointWorkflow workflow = stopPointWorkflowService.save(stopPointWorkflow);

    sePoDiClientService.updateStopPointStatusToDraft(sePoDiAdminClient, stopPointWorkflow);
    notificationService.sendCanceledStopPointWorkflowMail(workflow, stopPointCancelWorkflowModel.getMotivationComment());
    return workflow;
  }

  @MethodLogged(workflowType = LoggingAspect.RESTART_WORKFLOW)
  public StopPointWorkflow restartWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = stopPointWorkflowService.findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(), REJECTED);

    addBavExaminantDecision(restartWorkflowModel, stopPointWorkflow);
    StopPointWorkflow newStopPointWorkflow = cloneCurrentStopPointWorkflow(restartWorkflowModel, stopPointWorkflow);

    updateCurrentWorkflow(stopPointWorkflow, newStopPointWorkflow);

    //Add test here to check if failed
    //sePoDiClientService.updateStopPointStatusToDraftAsAdmin(newStopPointWorkflow);

    sePoDiClientService.updateDesignationOfficialServicePoint(sePoDiAdminClient, newStopPointWorkflow);

    //sePoDiClientService.updateStopPointStatusToInReviewAsAdmin(newStopPointWorkflow.getSloid(), newStopPointWorkflow.getVersionId());

    notificationService.sendRestartStopPointWorkflowMail(stopPointWorkflow, newStopPointWorkflow);
    return newStopPointWorkflow;
  }

  @MethodLogged(workflowType = LoggingAspect.WORKFLOW_TYPE_VOTE_WORKFLOW)
  public void progressWorkflowWithNewDecision(Long workflowId) {
    StopPointWorkflow workflow = stopPointWorkflowService.findStopPointWorkflow(workflowId);
    stopPointWorkflowService.validateIsStopPointInHearing(workflow);
    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = buildProgressDecider(workflow);

    stopPointWorkflowProgressDecider.calculateNewWorkflowStatus().ifPresent(newStatus -> {
      if (newStatus == WorkflowStatus.APPROVED) {
        sePoDiClientService.updateStopPointStatusToValidatedAsAdmin(workflow);
        notificationService.sendApprovedStopPointWorkflowMail(workflow);
      }
      if (newStatus == WorkflowStatus.REJECTED) {
        sePoDiClientService.updateStopPointStatusToDraft(sePoDiClient, workflow);
        notificationService.sendCanceledStopPointWorkflowMail(workflow, stopPointWorkflowProgressDecider.getRejectComment());
      }
      workflow.setEndDate(LocalDate.now());
      workflow.setStatus(newStatus);
      stopPointWorkflowService.save(workflow);
    });
  }

  private StopPointWorkflowProgressDecider buildProgressDecider(StopPointWorkflow workflow) {
    Map<Person, Optional<Decision>> decisions = new HashMap<>();
    workflow.getExaminants().forEach(examinant -> {
      Optional<Decision> decisionByExaminantId = decisionService.findDecisionByExaminantId(examinant.getId());
      decisions.put(examinant, decisionByExaminantId);
    });
    return new StopPointWorkflowProgressDecider(decisions);
  }

  private StopPointWorkflow cloneCurrentStopPointWorkflow(StopPointRestartWorkflowModel restartWorkflowModel,
      StopPointWorkflow stopPointWorkflow) {
    StopPointWorkflow newStopPointWorkflow = stopPointWorkflow.toBuilder()
        .id(null)
        .ccEmails(new ArrayList<>(stopPointWorkflow.getCcEmails()))
        .designationOfficial(restartWorkflowModel.getDesignationOfficial())
        .workflowComment(restartWorkflowModel.getMotivationComment()).startDate(LocalDate.now())
        .endDate(LocalDate.now().plusMonths(1)).build();
    Set<Person> examinantsCopy = new HashSet<>();
    stopPointWorkflow.getExaminants().forEach(person -> examinantsCopy.add(person.toBuilder().id(null).build()));
    newStopPointWorkflow.setExaminants(examinantsCopy);
    return stopPointWorkflowService.save(newStopPointWorkflow);
  }

  private StopPointWorkflow createStopPointAddWorkflow(StopPointAddWorkflowModel workflowStartModel,
      ReadServicePointVersionModel servicePointVersionModel) {
    SwissCanton swissCanton = servicePointVersionModel.getServicePointGeolocation().getSwissLocation().getCanton();
    List<StopPointClientPersonModel> personModels = examinants.getExaminants(swissCanton);
    return StopPointWorkflowMapper.addStopPointWorkflowToEntity(workflowStartModel, servicePointVersionModel, personModels);
  }

  private void updateCurrentWorkflow(StopPointWorkflow stopPointWorkflow, StopPointWorkflow newStopPointWorkflow) {
    stopPointWorkflow.setEndDate(LocalDate.now());
    stopPointWorkflow.setStatus(REJECTED);
    stopPointWorkflow.setFollowUpWorkflow(newStopPointWorkflow);
    stopPointWorkflowService.save(stopPointWorkflow);
  }

  private void addBavExaminantDecision(StopPointRestartWorkflowModel restartWorkflowModel, StopPointWorkflow stopPointWorkflow) {
    Person examinantBAV = PersonMapper.toPersonEntity(restartWorkflowModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    decisionService.createRestartDecision(examinantBAV, restartWorkflowModel.getMotivationComment());
  }

}
