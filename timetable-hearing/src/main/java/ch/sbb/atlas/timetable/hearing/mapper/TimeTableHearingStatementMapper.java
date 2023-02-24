package ch.sbb.atlas.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.StatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.timetable.hearing.entity.StatementSender;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
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
        .statementSender(StatementSender.builder()
            .firstName(statementModel.getStatementSender().getFirstName())
            .lastName(statementModel.getStatementSender().getLastName())
            .organisation(statementModel.getStatementSender().getOrganisation())
            .street(statementModel.getStatementSender().getStreet())
            .zip(statementModel.getStatementSender().getZip())
            .city(statementModel.getStatementSender().getCity())
            .email(statementModel.getStatementSender().getEmail())
            .build())
        .statement(statementModel.getStatement())
        .documents(statementModel.getDocuments())
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
        .statementSender(mapToSender(statement.getStatementSender()))
        .statement(statement.getStatement())
        .documents(statement.getDocuments())
        .justification(statement.getJustification())
        .etagVersion(statement.getVersion())
        .build();
  }

  private static StatementSenderModel mapToSender(StatementSender statementSender) {
    return StatementSenderModel.builder()
        .firstName(statementSender.getFirstName())
        .lastName(statementSender.getLastName())
        .organisation(statementSender.getOrganisation())
        .street(statementSender.getStreet())
        .zip(statementSender.getZip())
        .city(statementSender.getCity())
        .email(statementSender.getEmail())
        .build();
  }
}
