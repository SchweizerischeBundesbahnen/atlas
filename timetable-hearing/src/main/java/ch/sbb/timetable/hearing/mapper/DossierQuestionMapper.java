package ch.sbb.timetable.hearing.mapper;

import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DossierQuestionMapper {

  public static DossierQuestion toEntity(TthDossierQuestionModel model, Dossier dossier) {
    return DossierQuestion.builder()
        .id(model.getId())
        .dossier(dossier)
        .question(model.getQuestion())
        .answerToCanton(model.getAnswerToCanton())
        .version(model.getEtagVersion())
        .build();
  }

  public static TthDossierQuestionModel toModel(DossierQuestion entity) {
    return TthDossierQuestionModel.builder()
        .id(entity.getId())
        .question(entity.getQuestion())
        .answerToCanton(entity.getAnswerToCanton())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .editionDate(entity.getEditionDate())
        .editor(entity.getEditor())
        .etagVersion(entity.getVersion())
        .build();
  }

}
