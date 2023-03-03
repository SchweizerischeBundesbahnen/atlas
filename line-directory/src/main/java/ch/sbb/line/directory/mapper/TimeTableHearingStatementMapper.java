package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeTableHearingStatementMapper {

  public static TimetableHearingStatement toEntity(TimetableHearingStatementModel statementModel) {
    TimetableHearingStatement timetableHearingStatement = TimetableHearingStatement.builder()
        .id(statementModel.getId())
        .statementStatus(statementModel.getStatementStatus())
        .timetableYear(statementModel.getTimetableYear())
        .ttfnid(statementModel.getTtfnid())
        .swissCanton(statementModel.getSwissCanton())
        .stopPlace(statementModel.getStopPlace())
        .statementSender(StatementSenderMapper.toEntity(statementModel.getStatementSender()))
        .statement(statementModel.getStatement())
        .documents(statementModel.getDocuments().stream().map(StatementDocumentMapper::toEntity).collect(Collectors.toSet()))
        .justification(statementModel.getJustification())
        .version(statementModel.getEtagVersion())
        .build();
    timetableHearingStatement.setResponsibleTransportCompanies(
        statementModel.getResponsibleTransportCompanies().stream()
            .map(transportCompany -> ResponsibleTransportCompanyMapper.toEntity(transportCompany, timetableHearingStatement))
            .collect(Collectors.toSet()));
    return timetableHearingStatement;
  }

  public static TimetableHearingStatementModel toModel(TimetableHearingStatement statement) {
    return TimetableHearingStatementModel.builder()
        .id(statement.getId())
        .statementStatus(statement.getStatementStatus())
        .timetableYear(statement.getTimetableYear())
        .ttfnid(statement.getTtfnid())
        .swissCanton(statement.getSwissCanton())
        .stopPlace(statement.getStopPlace())
        .responsibleTransportCompanies(
            statement.getResponsibleTransportCompanies().stream().map(ResponsibleTransportCompanyMapper::toModel).toList())
        .statementSender(StatementSenderMapper.toModel(statement.getStatementSender()))
        .statement(statement.getStatement())
        .documents(statement.getDocuments().stream().map(StatementDocumentMapper::toModel).toList())
        .justification(statement.getJustification())
        .creationDate(statement.getCreationDate())
        .creator(statement.getCreator())
        .editionDate(statement.getEditionDate())
        .editor(statement.getEditor())
        .etagVersion(statement.getVersion())
        .build();
  }

}
