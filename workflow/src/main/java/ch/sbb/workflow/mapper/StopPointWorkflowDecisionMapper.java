package ch.sbb.workflow.mapper;

import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.model.sepodi.ReadDecisionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowDecisionMapper {

  public static ReadDecisionModel toModel(Decision entity) {
    return ReadDecisionModel.builder()
        .judgement(entity.getJudgement())
        .motivation(entity.getMotivation())
        .examinant(StopPointClientPersonMapper.toModel(entity.getExaminant()))
        .fotJudgement(entity.getFotJudgement())
        .fotMotivation(entity.getFotMotivation())
        .fotOverrider(StopPointClientPersonMapper.toModel(entity.getFotOverrider()))
        .build();
  }

}
