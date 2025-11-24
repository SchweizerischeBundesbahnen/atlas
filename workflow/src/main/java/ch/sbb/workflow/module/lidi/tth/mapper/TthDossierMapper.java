package ch.sbb.workflow.module.lidi.tth.mapper;

import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TthDossierMapper {

  public static TthDossier toEntity(TthDossierModel model) {
    return TthDossier.builder()
        .id(model.getId())
        .topic(model.getTopic())
        .dossierStatus(model.getDossierStatus())
        .internalComment(model.getInternalComment())
        .publicComment(model.getPublicComment())
        .statementIds(model.getStatementIds())
        .boContactMail(model.getBoContactMail())
        .boDeadlineToAnswer(model.getBoDeadlineToAnswer())
        .build();
  }

  public static TthDossierModel toModel(TthDossier entity) {
    return TthDossierModel.builder()
        .id(entity.getId())
        .topic(entity.getTopic())
        .dossierStatus(entity.getDossierStatus())
        .internalComment(entity.getInternalComment())
        .publicComment(entity.getPublicComment())
        .statementIds(entity.getStatementIds())
        .boContactMail(entity.getBoContactMail())
        .boDeadlineToAnswer(entity.getBoDeadlineToAnswer())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .editionDate(entity.getEditionDate())
        .editor(entity.getEditor())
        .build();
  }

}
