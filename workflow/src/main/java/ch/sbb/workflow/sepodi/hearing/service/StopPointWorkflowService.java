package ch.sbb.workflow.sepodi.hearing.service;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.DecisionType;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowAlreadyInAddedStatusException;
import ch.sbb.workflow.exception.StopPointWorkflowExaminantEmailNotUniqueException;
import ch.sbb.workflow.exception.StopPointWorkflowNotInHearingException;
import ch.sbb.workflow.exception.StopPointWorkflowStatusException;
import ch.sbb.workflow.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.sepodi.hearing.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.sepodi.hearing.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.AddExaminantsModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.DecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.Examinants;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.sepodi.hearing.repository.DecisionRepository;
import ch.sbb.workflow.sepodi.hearing.repository.StopPointWorkflowRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  private final StopPointWorkflowNotificationService notificationService;

  @Redacted
  public StopPointWorkflow getWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public Optional<StopPointWorkflow> getWorkflowByFollowUpId(Long id) {
    return workflowRepository.findStopPointWorkflowByFollowUpWorkflow(id);
  }

  @Redacted
  public Page<StopPointWorkflow> getWorkflows(StopPointWorkflowSearchRestrictions searchRestrictions) {
    return workflowRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public StopPointWorkflow editWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    if (workflowModel.getExaminants() != null && !workflowModel.getExaminants().isEmpty()) {
      checkIfAllExaminantEmailsAreUnique(workflowModel.getExaminants(), false);
    }
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);

    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new StopPointWorkflowStatusException(WorkflowStatus.ADDED);
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

  public List<StopPointClientPersonModel> getExaminantsByServicePointVersionId(Long servicePointVersionId) {
    ReadServicePointVersionModel servicePointVersionModel = sePoDiClientService.getServicePointById(servicePointVersionId);
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

  public StopPointWorkflow findStopPointWorkflowByPersonId(Long personId) {
    return workflowRepository.findByPersonId(personId).orElseThrow(() -> new IdNotFoundException(personId));
  }

  public void checkHasWorkflowAdded(Long versionId) {
    if (!workflowRepository.findAllByVersionIdAndStatus(versionId, WorkflowStatus.ADDED).isEmpty()) {
      throw new StopPointWorkflowAlreadyInAddedStatusException();
    }
  }

  public void checkIfAllExaminantEmailsAreUnique(List<StopPointClientPersonModel> examinants, boolean isAddWorkflow) {
    Set<String> emailSet = new HashSet<>();

    for (StopPointClientPersonModel examinant : examinants) {
      String email = examinant.getMail().toLowerCase();

      if(isAddWorkflow &&
        (email.equals(Examinants.NON_PROD_EMAIL_ATLAS.toLowerCase()) ||
        email.equals(Examinants.NON_PROD_EMAIL_CANTON.toLowerCase()))) {
          throw new StopPointWorkflowExaminantEmailNotUniqueException();
      }

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

  public StopPointWorkflow addExaminants(Long id, AddExaminantsModel addExaminantsModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);

    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new StopPointWorkflowStatusException(WorkflowStatus.HEARING);
    }

    List<StopPointClientPersonModel> persons = new ArrayList<>(stopPointWorkflow.getExaminants().stream()
        .map(StopPointClientPersonMapper::toModel).toList());
    persons.addAll(addExaminantsModel.getExaminants());
    checkIfAllExaminantEmailsAreUnique(persons, false);

    addExaminantsModel.getExaminants()
        .stream()
        .map(StopPointClientPersonMapper::toEntity)
        .forEach(examinant -> {
          examinant.setStopPointWorkflow(stopPointWorkflow);
          stopPointWorkflow.getExaminants().add(examinant);
        });

    addExaminantsModel.getCcEmails().stream()
        .filter(i -> stopPointWorkflow.getCcEmails().stream().noneMatch(i::equalsIgnoreCase))
        .forEach(stopPointWorkflow.getCcEmails()::add);

    List<String> addedExaminantMails = addExaminantsModel.getExaminants().stream().map(StopPointClientPersonModel::getMail).toList();
    notificationService.sendStartToAddedExaminant(stopPointWorkflow, addedExaminantMails);
    return save(stopPointWorkflow);
  }

}
