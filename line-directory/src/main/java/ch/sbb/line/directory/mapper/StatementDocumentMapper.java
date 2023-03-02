package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDocumentModel;
import ch.sbb.line.directory.entity.StatementDocument;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatementDocumentMapper {

  public static StatementDocument toEntity(TimetableHearingStatementDocumentModel timetableHearingStatementDocumentModel) {
    return StatementDocument.builder()
        .id(timetableHearingStatementDocumentModel.getId())
        .fileName(timetableHearingStatementDocumentModel.getFileName())
        .fileSize(timetableHearingStatementDocumentModel.getFileSize())
        .build();
  }

  public static TimetableHearingStatementDocumentModel toModel(StatementDocument statementDocument) {
    return TimetableHearingStatementDocumentModel.builder()
        .id(statementDocument.getId())
        .fileName(statementDocument.getFileName())
        .fileSize(statementDocument.getFileSize())
        .build();
  }
}
