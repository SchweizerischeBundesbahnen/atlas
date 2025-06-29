package ch.sbb.workflow.sepodi.hearing.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.redact.RedactBySboid;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.DecisionType;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.sepodi.hearing.repository.DecisionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class DecisionService {

  private final DecisionRepository decisionRepository;

  public void createRejectedDecision(Person examinant, String motivation) {
    Decision decision = createNoDecision(examinant, motivation, DecisionType.REJECTED);
    decisionRepository.save(decision);
  }

  public void createCanceledDecision(Person examinant, String motivation) {
    Decision decision = createNoDecision(examinant, motivation, DecisionType.CANCELED);
    decisionRepository.save(decision);
  }

  private Decision createNoDecision(Person examinant, String motivation, DecisionType decisionType) {
    return Decision.builder()
        .judgement(JudgementType.NO)
        .decisionType(decisionType)
        .examinant(examinant)
        .motivation(motivation)
        .motivationDate(LocalDateTime.now())
        .build();
  }

  public void createRestartDecision(Person examinant, String motivation) {
    Decision decision = Decision.builder()
        .judgement(JudgementType.NO)
        .decisionType(DecisionType.REJECTED)
        .examinant(examinant)
        .motivation(motivation)
        .motivationDate(LocalDateTime.now())
        .build();
    decisionRepository.save(decision);
  }

  public void save(Decision decision) {
    decisionRepository.save(decision);
  }

  public void addJudgementsToExaminants(List<StopPointClientPersonModel> examinants) {
    examinants.forEach(examinant -> {
      Decision decision = decisionRepository.findDecisionByExaminantId(examinant.getId());
      if (decision != null) {
        examinant.setJudgement(decision.getWeightedJudgement());
        examinant.setDecisionType(decision.getDecisionType());
      }
    });
  }

  @Redacted
  public Decision getDecisionByExaminantId(Long personId, @RedactBySboid(application = ApplicationType.SEPODI) String sboid) {
    Decision decision = decisionRepository.findDecisionByExaminantId(personId);
    if (decision == null) {
      throw new IdNotFoundException(personId);
    }
    return decision;
  }

  public Optional<Decision> findDecisionByExaminantId(Long examinantId) {
    return Optional.ofNullable(decisionRepository.findDecisionByExaminantId(examinantId));
  }

  public Set<Decision> findDecisionByWorkflowId(Long workflowId) {
    return decisionRepository.findDecisionByWorkflowId(workflowId);
  }
}
