package ch.sbb.workflow.module.lidi.tth.mapper;

import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TthDossierQuestionMapper {

  public static TthDossierQuestion toEntity(TthDossierQuestionModel model) {
    return TthDossierQuestion.builder()
        .id(model.getId())
        .question(model.getQuestion())
        .answerToCanton(model.getAnswerToCanton())
        .build();
  }

  public static TthDossierQuestionModel toModel(TthDossierQuestion entity) {
    return TthDossierQuestionModel.builder()
        .id(entity.getId())
        .question(entity.getQuestion())
        .answerToCanton(entity.getAnswerToCanton())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .editionDate(entity.getEditionDate())
        .editor(entity.getEditor())
        .build();
  }

}
