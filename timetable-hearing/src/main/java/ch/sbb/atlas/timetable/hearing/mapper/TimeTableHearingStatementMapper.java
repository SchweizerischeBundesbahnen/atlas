package ch.sbb.atlas.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeTableHearingStatementMapper {

  public static TimetableHearingStatement toEntity(TimetableHearingStatementModel statementModel) {
    return TimetableHearingStatement.builder()
        .id(statementModel.getId())
        .timetableYear(statementModel.getTimetableYear())
        .ttfnid(statementModel.getTtfnid())
        .swissCanton(statementModel.getSwissCanton())
        .stopPlace(statementModel.getStopPlace())
        .responsibleTransportCompanies(statementModel.getResponsibleTransportCompanies())
        .statementSender(StatementSenderMapper.toEntity(statementModel.getStatementSender()))
        .statement(statementModel.getStatement())
        .documents(statementModel.getDocuments().stream().map(StatementDocumentMapper::toEntity).collect(Collectors.toSet()))
        .justification(statementModel.getJustification())
        .version(statementModel.getEtagVersion())
        .build();
  }

  public static TimetableHearingStatementModel toModel(TimetableHearingStatement statement) {
    return TimetableHearingStatementModel.builder()
        .id(statement.getId())
        .timetableYear(statement.getTimetableYear())
        .ttfnid(statement.getTtfnid())
        .swissCanton(statement.getSwissCanton())
        .stopPlace(statement.getStopPlace())
        .responsibleTransportCompanies(statement.getResponsibleTransportCompanies())
        .statementSender(StatementSenderMapper.toModel(statement.getStatementSender()))
        .statement(statement.getStatement())
        .documents(statement.getDocuments().stream().map(StatementDocumentMapper::toModel).toList())
        .justification(statement.getJustification())
        .etagVersion(statement.getVersion())
        .build();
  }

}
