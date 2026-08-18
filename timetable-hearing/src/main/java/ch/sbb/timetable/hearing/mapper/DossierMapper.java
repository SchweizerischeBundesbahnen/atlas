package ch.sbb.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.timetable.hearing.entity.Dossier;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass
public class DossierMapper {

  public static Dossier toEntity(TthDossierModel model) {
    Dossier dossier = Dossier.builder()
        .id(model.getId())
        .swissCanton(model.getSwissCanton())
        .topic(model.getTopic())
        .internalComment(model.getInternalComment())
        .publicComment(model.getPublicComment())
        .statementIds(model.getStatementIds())
        .boContactMail(model.getBoContactMail())
        .boDeadlineToAnswer(model.getBoDeadlineToAnswer())
        .dossierStatus(model.getDossierStatus())
        .version(model.getEtagVersion())
        .build();

    dossier.setDossierQuestions(model.getQuestions().stream().map(i -> DossierQuestionMapper.toEntity(i, dossier)).toList());
    return dossier;
  }

  public static TthDossierModel toModel(Dossier entity) {
    return TthDossierModel.builder()
        .id(entity.getId())
        .swissCanton(entity.getSwissCanton())
        .topic(entity.getTopic())
        .dossierStatus(entity.getDossierStatus())
        .internalComment(entity.getInternalComment())
        .publicComment(entity.getPublicComment())
        .statementIds(entity.getStatementIds())
        .boContactMail(entity.getBoContactMail())
        .boDeadlineToAnswer(entity.getBoDeadlineToAnswer())
        .questions(entity.getDossierQuestions()
            .stream()
            .map(DossierQuestionMapper::toModel)
            .toList())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .editionDate(entity.getEditionDate())
        .editor(entity.getEditor())
        .etagVersion(entity.getVersion())
        .build();
  }

  public static BatchUpdateTimetableHearingStatementsModel toBatchUpdateModel(Dossier dossier) {
    return toBatchUpdateModel(dossier, dossier.getDossierStatus());
  }

  public static BatchUpdateTimetableHearingStatementsModel toBatchUpdateModel(Dossier dossier, DossierStatus newStatus) {
    BatchUpdateTimetableHearingStatementsModel batchUpdate = BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(dossier.getStatementIds())
        .dossierCanton(dossier.getSwissCanton())
        .publicComment(dossier.getPublicComment())
        .internalComment(dossier.getInternalComment())
        .topic(dossier.getTopic())
        .statementStatus(mapDossierStatusToStatementStatus(dossier, newStatus))
        .build();
    if (newStatus.forbidsUpdatesOnStatements()) {
      batchUpdate.setDossierId(dossier.getId());
      batchUpdate.setDossierContactMail(dossier.getBoContactMail());
      batchUpdate.setDossierContactSbbuid(dossier.getBoContactSbbuid());
    }
    return batchUpdate;
  }

  private static StatementStatus mapDossierStatusToStatementStatus(Dossier dossier, DossierStatus newStatus) {
    return switch (newStatus) {
      case CANCELED -> StatementStatus.RECEIVED;
      case ACCEPTED -> StatementStatus.ACCEPTED;
      case REJECTED -> StatementStatus.REJECTED;
      case MOVED -> StatementStatus.MOVED;
      case DISSOLVED -> {
        if (!Set.of(DossierStatus.ACCEPTED, DossierStatus.REJECTED, DossierStatus.MOVED).contains(dossier.getDossierStatus())) {
          throw SimpleAtlasException.builder()
              .status(HttpStatus.BAD_REQUEST)
              .messageAndError("DossierStatus " + dossier.getDossierStatus() + " has to be either of ACCEPTED, REJECTED or MOVED")
              .build();
        }
        yield mapDossierStatusToStatementStatus(dossier, dossier.getDossierStatus());
      }
      default -> StatementStatus.IN_REVIEW;
    };
  }
}