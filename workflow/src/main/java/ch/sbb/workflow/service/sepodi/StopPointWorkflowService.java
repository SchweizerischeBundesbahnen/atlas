package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.aop.Redacted;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowAlreadyInAddedStatusException;
import ch.sbb.workflow.exception.StopPointWorkflowExaminantEmailNotUniqueException;
import ch.sbb.workflow.exception.StopPointWorkflowNotInHearingException;
import ch.sbb.workflow.exception.StopPointWorkflowStatusMustBeAddedException;
import ch.sbb.workflow.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.Examinants;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private final StopPointWorkflowRepository workflowRepository;
  private final DecisionRepository decisionRepository;
  private final SePoDiClientService sePoDiClientService;
  private final Examinants examinants;

  @Redacted(redactedClassType = StopPointWorkflow.class)
  public StopPointWorkflow getWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public Optional<StopPointWorkflow> getWorkflowByFollowUpId(Long id) {
    return workflowRepository.findStopPointWorkflowByFollowUpWorkflow(id);
  }

  @Redacted(redactedClassType = StopPointWorkflow.class)
  public Page<StopPointWorkflow> getWorkflows(StopPointWorkflowSearchRestrictions searchRestrictions) {
    return workflowRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public StopPointWorkflow editWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    if (workflowModel.getExaminants() != null && !workflowModel.getExaminants().isEmpty()) {
      checkIfAllExaminantEmailsAreUnique(workflowModel.getExaminants());
    }
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);

    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new StopPointWorkflowStatusMustBeAddedException();
    }

    if (!stopPointWorkflow.getDesignationOfficial().equals(workflowModel.getDesignationOfficial())) {
      stopPointWorkflow.setDesignationOfficial(workflowModel.getDesignationOfficial());
      sePoDiClientService.updateDesignationOfficialServicePoint(stopPointWorkflow);
    }

    stopPointWorkflow.setWorkflowComment(workflowModel.getWorkflowComment());
    stopPointWorkflow.setCcEmails(workflowModel.getCcEmails());

    if (workflowModel.getExaminants() != null) {
      stopPointWorkflow.getExaminants().clear();
      workflowModel.getExaminants()
          .stream()
          .map(StopPointClientPersonMapper::toEntity)
          .forEach(examinant -> {
            examinant.setStopPointWorkflow(stopPointWorkflow);
            stopPointWorkflow.getExaminants().add(examinant);
          });
    }
    return save(stopPointWorkflow);
  }

  public List<StopPointClientPersonModel> getExaminants(Long id) {
    ReadServicePointVersionModel servicePointVersionModel = sePoDiClientService.getServicePointById(id);
    return examinants.getExaminants(servicePointVersionModel.getServicePointGeolocation().getSwissLocation().getCanton());
  }

  public void voteWorkFlow(Long id, Long personId, DecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    validateIsStopPointInHearing(stopPointWorkflow);
    Person examinant = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(personId));

    updateExaminantInformation(decisionModel, examinant);

    voteDecision(decisionModel, examinant);
  }

  public void overrideVoteWorkflow(Long id, Long personId, OverrideDecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    validateIsStopPointInHearing(stopPointWorkflow);
    Person examinant = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(personId));
    voteOverrideDecision(decisionModel, examinant);
  }

  public StopPointWorkflow findStopPointWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public void checkHasWorkflowAdded(Long versionId) {
    if (!workflowRepository.findAllByVersionIdAndStatus(versionId, WorkflowStatus.ADDED).isEmpty()) {
      throw new StopPointWorkflowAlreadyInAddedStatusException();
    }
  }

  public void checkIfAllExaminantEmailsAreUnique(List<StopPointClientPersonModel> examinants) {
    Set<String> emailSet = new HashSet<>();
    for (StopPointClientPersonModel examinant : examinants) {
      String email = examinant.getMail().toLowerCase();
      if (!emailSet.add(email)) {
          throw new StopPointWorkflowExaminantEmailNotUniqueException();
      }
    }
  }

  public StopPointWorkflow save(StopPointWorkflow stopPointWorkflow) {
    return workflowRepository.saveAndFlush(stopPointWorkflow);
  }

  public void validateIsStopPointInHearing(StopPointWorkflow stopPointWorkflow) {
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new StopPointWorkflowNotInHearingException();
    }
  }

  public List<StopPointWorkflow> findWorkflowsInHearing() {
    return workflowRepository.findWorkflowsByStatus(WorkflowStatus.HEARING);
  }

  private void voteDecision(DecisionModel decisionModel, Person examinant) {
    Decision decision = decisionRepository.findDecisionByExaminantId(examinant.getId());
    if (decision == null) {
      decision = new Decision();
    }
    decision.setDecisionType(DecisionType.VOTED);
    decision.setJudgement(decisionModel.getJudgement());
    decision.setExaminant(examinant);
    decision.setMotivation(decisionModel.getMotivation());
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);
  }

  public void voteExpiredWorkflowDecision(Person examinant) {
    Decision decision = new Decision();
    decision.setDecisionType(DecisionType.VOTED_EXPIRATION);
    decision.setJudgement(JudgementType.YES);
    decision.setExaminant(examinant);
    decision.setMotivationDate(LocalDateTime.now());
    decisionRepository.save(decision);
  }

  private void voteOverrideDecision(OverrideDecisionModel decisionModel, Person examinant) {
    Decision decision = decisionRepository.findDecisionByExaminantId(examinant.getId());
    if (decision == null) {
      decision = new Decision();
    }
    decision.setExaminant(examinant);
    decision.setFotMotivationDate(LocalDateTime.now());
    decision.setFotMotivation(decisionModel.getFotMotivation());
    decision.setFotJudgement(decisionModel.getFotJudgement());
    Person fotOverrider = Person.builder()
        .firstName(decisionModel.getFirstName())
        .lastName(decisionModel.getLastName())
        .build();
    decision.setFotOverrider(fotOverrider);
    decisionRepository.save(decision);
  }

  private void updateExaminantInformation(DecisionModel decisionModel, Person examinant) {
    examinant.setFirstName(decisionModel.getFirstName());
    examinant.setLastName(decisionModel.getLastName());
    examinant.setOrganisation(decisionModel.getOrganisation());
    examinant.setFunction(decisionModel.getPersonFunction());
  }

}
