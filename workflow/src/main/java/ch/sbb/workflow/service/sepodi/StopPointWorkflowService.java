package ch.sbb.workflow.service.sepodi;

import static ch.sbb.atlas.workflow.model.WorkflowStatus.REJECTED;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Otp;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowAlreadyInAddedStatusException;
import ch.sbb.workflow.helper.OtpHelper;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.mapper.ClientPersonMapper;
import ch.sbb.workflow.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.Examinants;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.workflow.DecisionRepository;
import ch.sbb.workflow.workflow.OtpRepository;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private static final String EXCEPTION_MSG = "Workflow status must be ADDED!!!(REPLACE ME WITH A CUSTOM EXCEPTION)";
  private static final String EXCEPTION_HEARING_MSG = "Workflow status must be HEARING!!!(REPLACE ME WITH A CUSTOM EXCEPTION)";
  private static final int WORKFLOW_DURATION_IN_DAYS = 31;

  private final StopPointWorkflowRepository workflowRepository;
  private final DecisionService decisionService;
  private final DecisionRepository decisionRepository;
  private final OtpRepository otpRepository;
  private final SePoDiClientService sePoDiClientService;
  private final Examinants examinants;
  private final StopPointWorkflowNotificationService notificationService;

  public StopPointWorkflow getWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public Page<StopPointWorkflow> getWorkflows(StopPointWorkflowSearchRestrictions searchRestrictions) {
    return workflowRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public StopPointWorkflow addWorkflow(StopPointAddWorkflowModel stopPointAddWorkflowModel) {
    if (hasWorkflowAdded(stopPointAddWorkflowModel.getVersionId())) {
      throw new StopPointWorkflowAlreadyInAddedStatusException();
    }
    ReadServicePointVersionModel servicePointVersionModel = sePoDiClientService.updateStopPointStatusToInReview(
        stopPointAddWorkflowModel.getSloid(), stopPointAddWorkflowModel.getVersionId());
    StopPointWorkflow stopPointWorkflow = mapStopPointWorkflow(stopPointAddWorkflowModel, servicePointVersionModel);
    stopPointWorkflow.setSboid(servicePointVersionModel.getBusinessOrganisation());
    stopPointWorkflow.setLocalityName(
        servicePointVersionModel.getServicePointGeolocation().getSwissLocation().getLocalityMunicipality().getLocalityName());
    stopPointWorkflow.setDesignationOfficial(servicePointVersionModel.getDesignationOfficial());
    stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
    stopPointWorkflow.setVersionValidFrom(servicePointVersionModel.getValidFrom());
    return workflowRepository.save(stopPointWorkflow);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public StopPointWorkflow startWorkflow(Long id) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(),
        WorkflowStatus.HEARING);
    stopPointWorkflow.setStatus(WorkflowStatus.HEARING);
    stopPointWorkflow.setEndDate(LocalDate.now().plusDays(WORKFLOW_DURATION_IN_DAYS));
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    notificationService.sendStartStopPointWorkflowMail(workflow);
    return workflow;
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService."
          + "isAtLeastSupervisor( T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public StopPointWorkflow rejectWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(stopPointWorkflow.getStatus(), REJECTED);
    Person examinantBAV = ClientPersonMapper.toEntity(workflowModel.getExaminantBAVClient());
    decisionService.createRejectedDecision(examinantBAV, workflowModel.getMotivationComment());
    sePoDiClientService.updateStoPointStatusToDraft(stopPointWorkflow);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    stopPointWorkflow.setStatus(REJECTED);
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    notificationService.sendRejectPointWorkflowMail(workflow);
    return stopPointWorkflow;
  }

  public StopPointWorkflow editWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (!stopPointWorkflow.getDesignationOfficial().equals(workflowModel.getDesignationOfficial())
        && stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException(EXCEPTION_MSG);
    }
    stopPointWorkflow.setWorkflowComment(workflowModel.getWorkflowComment());
    return workflowRepository.save(stopPointWorkflow);
  }

  public StopPointWorkflow cancelWorkflow(Long id, StopPointRejectWorkflowModel stopPointCancelWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException(EXCEPTION_HEARING_MSG);
    }
    ClientPersonModel examinantBAVclientPersonModel = stopPointCancelWorkflowModel.getExaminantBAVClient();
    Person examinantBAV = ClientPersonMapper.toEntity(examinantBAVclientPersonModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    Decision decision = new Decision();
    decision.setJudgement(JudgementType.NO);
    decision.setDecisionType(DecisionType.CANCELED);
    decision.setExaminant(examinantBAV);
    decision.setMotivation(stopPointCancelWorkflowModel.getMotivationComment());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);

    stopPointWorkflow.setStatus(WorkflowStatus.CANCELED);
    return stopPointWorkflow;
  }

  public StopPointWorkflow restartWorkflow(Long id, StopPointRestartWorkflowModel restartWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException(EXCEPTION_HEARING_MSG);
    }
    //String newDesignationOfficial = restartWorkflowModel.getNewDesignationOfficial();
    //TODO: sePoDiClient.update(officialDesignation)

    ClientPersonModel examinantBAVclientPersonModel = restartWorkflowModel.getExaminantBAVClient();
    Person examinantBAV = ClientPersonMapper.toEntity(examinantBAVclientPersonModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    Decision decision = new Decision();
    decision.setDecisionType(DecisionType.RESTARTED);
    decision.setExaminant(examinantBAV);
    decision.setMotivation(restartWorkflowModel.getMotivationComment());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);

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
    workflowRepository.save(newStopPointWorkflow);
    //update current workflow
    stopPointWorkflow.setStatus(REJECTED);
    stopPointWorkflow.setFollowUpWorkflow(newStopPointWorkflow);
    workflowRepository.save(stopPointWorkflow);
    return newStopPointWorkflow;
  }

  public StopPointWorkflow addExaminantToWorkflow(Long id, StopPointClientPersonModel personModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person examinant = StopPointClientPersonMapper.toEntity(personModel);
      stopPointWorkflow.getExaminants().add(examinant);
      examinant.setStopPointWorkflow(stopPointWorkflow);
      return workflowRepository.save(stopPointWorkflow);
    }
    throw new IllegalStateException(EXCEPTION_MSG);
  }

  public StopPointWorkflow removeExaminantToWorkflow(Long id, Long personId) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person person = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
          .orElseThrow(() -> new IdNotFoundException(personId));
      stopPointWorkflow.getExaminants().remove(person);
      return workflowRepository.save(stopPointWorkflow);
    }
    throw new IllegalStateException(EXCEPTION_MSG);
  }

  public void obtainOtp(Long id, Long personId) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != REJECTED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person person = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
          .orElseThrow(() -> new IdNotFoundException(personId));
      Otp otp = Otp.builder()
          .person(person)
          .code(OtpHelper.generateCode())
          .build();
      otpRepository.save(otp);
    } else {
      throw new IllegalStateException(EXCEPTION_MSG);
    }
  }

  public void voteWorkFlow(Long id, Long personId, DecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException(EXCEPTION_HEARING_MSG);
    }
    Person examinant = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(personId));
    validatePinCode(decisionModel, examinant);
    Decision decision = new Decision();
    decision.setDecisionType(DecisionType.VOTED);
    decision.setJudgement(decisionModel.getJudgement());
    decision.setExaminant(examinant);
    decision.setMotivation(decisionModel.getMotivation());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);
  }

  public void overrideVoteWorkflow(Long id, Long personId, OverrideDecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException(EXCEPTION_HEARING_MSG);
    }
    Person examinant = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(personId));
    Decision decision = decisionRepository.findDecisionByExaminantId(examinant.getId());
    if (decision == null) {
      decision = new Decision();
    }
    decision.setExaminant(examinant);
    decision.setFotMotivationDate(LocalDateTime.now());
    decision.setFotMotivation(decisionModel.getFotMotivation());
    decision.setFotJudgement(decisionModel.getFotJudgement());
    Person fotOverrider = ClientPersonMapper.toEntity(decisionModel.getOverrideExaminant());
    decision.setFotOverrider(fotOverrider);
    decisionRepository.save(decision);
  }

  void validatePinCode(DecisionModel decisionModel, Person examinant) {
    Otp otp = otpRepository.findByPersonId(examinant.getId());
    if (!otp.getCode().equals(decisionModel.getPinCode())) {
      throw new IllegalStateException("Wrong pin code");
    }
  }

  private StopPointWorkflow findStopPointWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  private StopPointWorkflow mapStopPointWorkflow(StopPointAddWorkflowModel workflowStartModel,
      ReadServicePointVersionModel servicePointVersionModel) {
    StopPointClientPersonModel examinantPersonByCanton =
        examinants.getExaminantPersonByCanton(
            servicePointVersionModel.getServicePointGeolocation().getSwissLocation().getCanton());
    StopPointClientPersonModel examinantSpecialistOffice = examinants.getExaminantSpecialistOffice();
    List<StopPointClientPersonModel> personModels = new ArrayList<>();
    personModels.add(examinantSpecialistOffice);
    personModels.add(examinantPersonByCanton);
    return StopPointWorkflowMapper.toEntity(workflowStartModel, personModels);
  }

  private boolean hasWorkflowAdded(Long businessObjectId) {
    return !workflowRepository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.ADDED).isEmpty();
  }

  private boolean hasWorkflowHearing(Long businessObjectId) {
    return !workflowRepository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.HEARING).isEmpty();
  }

}
