package ch.sbb.atlas.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.StatementDocumentModel;
import ch.sbb.atlas.timetable.hearing.entity.StatementDocument;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatementDocumentMapper {

  public static StatementDocument toEntity(StatementDocumentModel statementDocumentModel) {
    return StatementDocument.builder()
        .id(statementDocumentModel.getId())
        .fileName(statementDocumentModel.getFileName())
        .fileSize(statementDocumentModel.getFileSize())
        .build();
  }

  public static StatementDocumentModel toModel(StatementDocument statementDocument) {
    return StatementDocumentModel.builder()
        .id(statementDocument.getId())
        .fileName(statementDocument.getFileName())
        .fileSize(statementDocument.getFileSize())
        .build();
  }
}
