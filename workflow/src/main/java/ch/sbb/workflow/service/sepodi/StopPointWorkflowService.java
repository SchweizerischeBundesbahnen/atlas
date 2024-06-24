package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.aop.Redacted;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowAlreadyInAddedStatusException;
import ch.sbb.workflow.exception.StopPointWorkflowNotInHearingException;
import ch.sbb.workflow.mapper.ClientPersonMapper;
import ch.sbb.workflow.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private static final String EXCEPTION_MSG = "Workflow status must be ADDED!!!(REPLACE ME WITH A CUSTOM EXCEPTION)";

  private final StopPointWorkflowRepository workflowRepository;
  private final DecisionRepository decisionRepository;

  @Redacted(redactedClassType = StopPointWorkflow.class)
  public StopPointWorkflow getWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  @Redacted(redactedClassType = StopPointWorkflow.class)
  public Page<StopPointWorkflow> getWorkflows(StopPointWorkflowSearchRestrictions searchRestrictions) {
    return workflowRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public StopPointWorkflow editWorkflow(Long id, EditStopPointWorkflowModel workflowModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (!stopPointWorkflow.getDesignationOfficial().equals(workflowModel.getDesignationOfficial())
        && stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException(EXCEPTION_MSG);
    }
    stopPointWorkflow.setWorkflowComment(workflowModel.getWorkflowComment());
    return save(stopPointWorkflow);
  }

  public StopPointWorkflow addExaminantToWorkflow(Long id, StopPointClientPersonModel personModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person examinant = StopPointClientPersonMapper.toEntity(personModel);
      stopPointWorkflow.getExaminants().add(examinant);
      examinant.setStopPointWorkflow(stopPointWorkflow);
      return save(stopPointWorkflow);
    }
    throw new IllegalStateException(EXCEPTION_MSG);
  }

  public StopPointWorkflow removeExaminantToWorkflow(Long id, Long personId) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED || stopPointWorkflow.getStatus() != WorkflowStatus.APPROVED) {
      Person person = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
          .orElseThrow(() -> new IdNotFoundException(personId));
      stopPointWorkflow.getExaminants().remove(person);
      return save(stopPointWorkflow);
    }
    throw new IllegalStateException(EXCEPTION_MSG);
  }

  public void voteWorkFlow(Long id, Long personId, DecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new StopPointWorkflowNotInHearingException();
    }
    Person examinant = stopPointWorkflow.getExaminants().stream().filter(p -> p.getId().equals(personId)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(personId));

    updateExaminantInformation(decisionModel, examinant);

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

  private static void updateExaminantInformation(DecisionModel decisionModel, Person examinant) {
    examinant.setFirstName(decisionModel.getFirstName());
    examinant.setLastName(decisionModel.getLastName());
    examinant.setOrganisation(decisionModel.getOrganisation());
    examinant.setFunction(decisionModel.getPersonFunction());
  }

  public void overrideVoteWorkflow(Long id, Long personId, OverrideDecisionModel decisionModel) {
    StopPointWorkflow stopPointWorkflow = findStopPointWorkflow(id);
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new StopPointWorkflowNotInHearingException();
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

  public StopPointWorkflow findStopPointWorkflow(Long id) {
    return workflowRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public void checkHasWorkflowAdded(Long versionId) {
    if (!workflowRepository.findAllByVersionIdAndStatus(versionId, WorkflowStatus.ADDED).isEmpty()) {
      throw new StopPointWorkflowAlreadyInAddedStatusException();
    }
  }

  private boolean hasWorkflowHearing(Long businessObjectId) {
    return !workflowRepository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.HEARING).isEmpty();
  }

  public StopPointWorkflow save(StopPointWorkflow stopPointWorkflow) {
    return workflowRepository.saveAndFlush(stopPointWorkflow);
  }

}
