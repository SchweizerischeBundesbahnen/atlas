package ch.sbb.workflow.sepodi.termination.mapper;

import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
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
        .creationDate(decision.getCreationDate())
        .creator(decision.getCreator())
        .editor(decision.getEditor())
        .editionDate(decision.getEditionDate())
        .build();
  }

}
