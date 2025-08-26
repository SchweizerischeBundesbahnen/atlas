package ch.sbb.workflow.module.sepodi.termination.mapper;

import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationDecisionMapper {

  public static TerminationDecision toEntity(TerminationDecisionModel model) {
    return TerminationDecision.builder()
        .judgement(model.getJudgement())
        .terminationDecisionPerson(model.getTerminationDecisionPerson())
        .motivation(model.getMotivation())
        .build();
  }

  public static TerminationDecisionModel toModel(TerminationDecision decision) {
    return TerminationDecisionModel.builder()
        .terminationDecisionPerson(decision.getTerminationDecisionPerson())
        .judgement(decision.getJudgement())
        .motivation(decision.getMotivation())
        .build();
  }

}
