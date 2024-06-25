package ch.sbb.workflow.service.sepodi;

import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class DecisionService {

  private final DecisionRepository decisionRepository;

  public Decision createRejectedDecision(Person examinant, String motivation){
    Decision decision = Decision.builder()
        .judgement(JudgementType.NO)
        .decisionType(DecisionType.REJECTED)
        .examinant(examinant)
        .motivation(motivation)
        .motivationDate(LocalDateTime.now())
        .build();
    return decisionRepository.save(decision);
  }

  public void save(Decision decision) {
    decisionRepository.save(decision);
  }

  public void addJudgementsToExaminants(List<StopPointClientPersonModel> examinants) {
    examinants.forEach(examinant -> {
      Decision decision = decisionRepository.findDecisionByExaminantId(examinant.getId());
      if (decision != null) {
        if (decision.getFotJudgement() == null) {
          examinant.setJudgement(decision.getJudgement());
        } else {
          examinant.setJudgement(decision.getFotJudgement());
        }
      }
    });
  }
}
