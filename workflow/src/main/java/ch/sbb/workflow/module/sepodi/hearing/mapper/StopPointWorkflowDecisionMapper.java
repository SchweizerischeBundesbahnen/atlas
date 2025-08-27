package ch.sbb.workflow.module.sepodi.hearing.mapper;

import ch.sbb.workflow.module.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.ReadDecisionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowDecisionMapper {

  public static ReadDecisionModel toModel(Decision entity) {
    ReadDecisionModel readDecisionModel = ReadDecisionModel.builder()
        .judgement(entity.getJudgement())
        .motivation(entity.getMotivation())
        .examinant(StopPointClientPersonMapper.toModel(entity.getExaminant()))
        .fotJudgement(entity.getFotJudgement())
        .fotMotivation(entity.getFotMotivation())
        .decisionType(entity.getDecisionType())
        .build();
    if (entity.getFotOverrider() != null) {
      readDecisionModel.setFotOverrider(StopPointClientPersonMapper.toModel(entity.getFotOverrider()));
    }
    return readDecisionModel;
  }

}
