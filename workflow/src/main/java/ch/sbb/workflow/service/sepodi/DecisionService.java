package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.workflow.aop.Redacted;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  @Redacted(redactedClassType = Decision.class)
  public Decision getDecisionByExaminantId(Long personId) {
    Decision decision = decisionRepository.findDecisionByExaminantId(personId);
    if (decision == null) {
      throw new IdNotFoundException(personId);
    }
    return decision;
  }

  public Optional<Decision> findDecisionByExaminantId(Long examinantId) {
    return Optional.ofNullable(decisionRepository.findDecisionByExaminantId(examinantId));
  }
}
