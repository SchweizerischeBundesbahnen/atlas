package ch.sbb.workflow.service;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.DecisionModel;
import ch.sbb.atlas.api.workflow.OverrideDecisionModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.api.workflow.StopPointRejectWorkflowModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.Otp;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.mapper.ClientPersonMapper;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.Examinants;
import ch.sbb.workflow.workflow.DecisionRepository;
import ch.sbb.workflow.workflow.OtpRepository;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private final StopPointWorkflowRepository workflowRepository;
  private final DecisionRepository decisionRepository;
  private final OtpRepository otpRepository;
  private final SePoDiClient sePoDiClient;

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
      // TODO: WorkflowCurrentlyAddedException
      throw new IllegalStateException("Workflow already in Hearing!");
    }
    //TODO: extract me in a SePoDiService
    UpdateServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsImport(
            stopPointWorkflow.getVersionId(), Status.IN_REVIEW)
        .getBody();
    if (Objects.requireNonNull(updateServicePointVersionModel).getStatus() == Status.IN_REVIEW) {
      stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
      return workflowRepository.save(stopPointWorkflow);
    }
    throw new IllegalStateException("Something went wrong!");
  }

  public StopPointWorkflow startWorkflow(Long id) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    // TODO: WorkflowCurrentlyInHearingException
    if (hasWorkflowHearing(stopPointWorkflow.getVersionId())) {
      throw new IllegalStateException("Workflow already in Hearing!");
    }
    // TODO: WorkflowCurrentlyAddedException
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException("Workflow status must be ADDED!!!");
    }
    stopPointWorkflow.setStatus(WorkflowStatus.HEARING);
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    notificationService.sendStopPointWorkflowMail(workflow);
    return workflow;
  }

  public StopPointWorkflow editWorkflow(Long id, StopPointAddWorkflowModel workflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (!stopPointWorkflow.getDesignationOfficial().equals(workflowModel.getDesignationOfficial())) {
      if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
        throw new IllegalStateException("Workflow status must be ADDED!!!");
      }
      //TODO: 1. sePoDiClient.update(officialDesignation)
      //create new Workflow
      StopPointWorkflow newStopPointWorkflow = mapStopPointWorkflow(workflowModel);
      newStopPointWorkflow.setStatus(WorkflowStatus.ADDED);
      StopPointWorkflow newWorkflow = workflowRepository.save(newStopPointWorkflow);

      stopPointWorkflow.setStatus(WorkflowStatus.REJECTED);
      stopPointWorkflow.setFollowUpWorkflow(newWorkflow);
      workflowRepository.save(stopPointWorkflow);
      return newWorkflow;
    }
    return workflowRepository.save(stopPointWorkflow);
  }

  public StopPointWorkflow rejectWorkflow(Long id, StopPointRejectWorkflowModel workflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException("Workflow status must be ADDED!!!");
    }
    ClientPersonModel examinantBAVclientPersonModel = workflowModel.getExaminantBAVClient();
    Person examinantBAV = ClientPersonMapper.toEntity(examinantBAVclientPersonModel);
    examinantBAV.setStopPointWorkflow(stopPointWorkflow);
    Decision decision = new Decision();
    decision.setJudgement(false);
    decision.setExaminant(examinantBAV);
    decision.setMotivation(workflowModel.getMotivationComment());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);

    //TODO: 1. sePoDiClient.update(officialDesignation)
    stopPointWorkflow.setStatus(WorkflowStatus.REJECTED);
    return stopPointWorkflow;
  }

  public StopPointWorkflow addExaminantToWorkflow(Long id, ClientPersonModel personModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person examinant = ClientPersonMapper.toEntity(personModel);
      stopPointWorkflow.getExaminants().add(examinant);
      examinant.setStopPointWorkflow(stopPointWorkflow);
      return workflowRepository.save(stopPointWorkflow);
    }
    throw new IllegalStateException("Workflow status must be ADDED!!!");
  }

  public StopPointWorkflow removeExaminantToWorkflow(Long id, Long personId) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person person = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
          .orElseThrow(() -> new IdNotFoundException(personId));
      stopPointWorkflow.getExaminants().remove(person);
      return workflowRepository.save(stopPointWorkflow);
    }
    throw new IllegalStateException("Workflow status must be ADDED!!!");
  }

  public void obtainOtp(Long id, Long personId) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.REJECTED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person person = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
          .orElseThrow(() -> new IdNotFoundException(personId));
      Otp otp = Otp.builder()
          .person(person)
          .code(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
          .build();
      otpRepository.save(otp);
    } else {
      throw new IllegalStateException("Workflow status must be ADDED!!!");
    }
  }

  public void voteWorkFlow(Long id, Long personId, DecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException("Workflow status must be HEARING!!!");
    }
    Person examinant = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(personId));
    validatePinCode(decisionModel, examinant);
    Decision decision = new Decision();
    decision.setJudgement(decisionModel.getJudgement());
    decision.setExaminant(examinant);
    decision.setMotivation(decisionModel.getMotivation());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);
  }

  public void overrideVoteWorkflow(Long id, Long personId, OverrideDecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new IllegalStateException("Workflow status must be HEARING!!!");
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
    List<ClientPersonModel> examinants = new ArrayList<>();
    examinants.add(examinantSpecialistOffice);
    examinants.add(examinantPersonByCanton);
    return StopPointWorkflowMapper.toEntity(workflowStartModel, examinants);
  }

  private boolean hasWorkflowAdded(Long businessObjectId) {
    return !workflowRepository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.ADDED).isEmpty();
  }

  private boolean hasWorkflowHearing(Long businessObjectId) {
    return !workflowRepository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.HEARING).isEmpty();
  }

}
