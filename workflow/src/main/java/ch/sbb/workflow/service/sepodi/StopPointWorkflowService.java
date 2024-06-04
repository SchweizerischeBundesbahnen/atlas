package ch.sbb.workflow.service.sepodi;

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
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.mapper.ClientPersonMapper;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.Examinants;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.workflow.DecisionRepository;
import ch.sbb.workflow.workflow.OtpRepository;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private static final String EXCEPTION_MSG = "Workflow status must be ADDED!!!(REPLACE ME WITH A CUSTOM EXCEPTION)";
  private static final String EXCEPTION_HEARING_MSG = "Workflow status must be HEARING!!!(REPLACE ME WITH A CUSTOM EXCEPTION)";

  private final StopPointWorkflowRepository workflowRepository;
  private final DecisionRepository decisionRepository;
  private final OtpRepository otpRepository;
  private final SePoDiClientService sePoDiClientService;
  private final Examinants examinants;
  private final WorkflowNotificationService notificationService;

  public StopPointWorkflow getWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<StopPointWorkflow> getWorkflows() {
    return workflowRepository.findAll();
  }

  public StopPointWorkflow addWorkflow(StopPointAddWorkflowModel stopPointAddWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = mapStopPointWorkflow(stopPointAddWorkflowModel);
    if (hasWorkflowAdded(stopPointWorkflow.getVersionId())) {
      throw new StopPointWorkflowAlreadyInAddedStatusException();
    }
    sePoDiClientService.updateStoPointStatusToInReview(stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId());
    stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
    return workflowRepository.save(stopPointWorkflow);
  }

  public StopPointWorkflow startWorkflow(Long id) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    // TODO: WorkflowCurrentlyInHearingException
    if (hasWorkflowHearing(stopPointWorkflow.getVersionId())) {
      throw new IllegalStateException("Workflow already in Hearing!");
    }
    // TODO: WorkflowCurrentlyAddedException
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException(EXCEPTION_MSG);
    }
    stopPointWorkflow.setStatus(WorkflowStatus.HEARING);
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    notificationService.sendStopPointWorkflowMail(workflow);
    return workflow;
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

  public StopPointWorkflow rejectWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException(EXCEPTION_MSG);
    }
    ClientPersonModel examinantBAVclientPersonModel = workflowModel.getExaminantBAVClient();
    Person examinantBAV = ClientPersonMapper.toEntity(examinantBAVclientPersonModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    Decision decision = new Decision();
    decision.setJudgement(JudgementType.NO);
    decision.setDecisionType(DecisionType.REJECTED);
    decision.setExaminant(examinantBAV);
    decision.setMotivation(workflowModel.getMotivationComment());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);

    //TODO: 1. sePoDiClient.update(officialDesignation)
    stopPointWorkflow.setStatus(WorkflowStatus.REJECTED);
    return stopPointWorkflow;
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
        .swissMunicipalityName(stopPointWorkflow.getSwissMunicipalityName())
        .startDate(stopPointWorkflow.getStartDate())//todo
        .endDate(stopPointWorkflow.getEndDate())
        .build();
    workflowRepository.save(newStopPointWorkflow);
    //update current workflow
    stopPointWorkflow.setStatus(WorkflowStatus.REJECTED);
    stopPointWorkflow.setFollowUpWorkflow(newStopPointWorkflow);
    workflowRepository.save(stopPointWorkflow);
    return newStopPointWorkflow;
  }

  public StopPointWorkflow addExaminantToWorkflow(Long id, ClientPersonModel personModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person examinant = ClientPersonMapper.toEntity(personModel);
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
    if (stopPointWorkflow.getStatus() != WorkflowStatus.REJECTED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
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

  private StopPointWorkflow mapStopPointWorkflow(StopPointAddWorkflowModel workflowStartModel) {
    ClientPersonModel examinantPersonByCanton = examinants.getExaminantPersonByCanton(workflowStartModel.getSwissCanton());
    ClientPersonModel examinantSpecialistOffice = examinants.getExaminantSpecialistOffice();
    List<ClientPersonModel> personModels = new ArrayList<>();
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
