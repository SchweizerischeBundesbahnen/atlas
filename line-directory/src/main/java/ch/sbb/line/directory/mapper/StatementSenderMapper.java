package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.line.directory.entity.StatementSender;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatementSenderMapper {

  public static StatementSender toEntity(TimetableHearingStatementSenderModel timetableHearingStatementSenderModel) {
    return StatementSender.builder()
        .firstName(timetableHearingStatementSenderModel.getFirstName())
        .lastName(timetableHearingStatementSenderModel.getLastName())
        .organisation(timetableHearingStatementSenderModel.getOrganisation())
        .street(timetableHearingStatementSenderModel.getStreet())
        .zip(timetableHearingStatementSenderModel.getZip())
        .city(timetableHearingStatementSenderModel.getCity())
        .email(timetableHearingStatementSenderModel.getEmail())
        .build();
  }

  public static TimetableHearingStatementSenderModel toModel(StatementSender statementSender) {
    return TimetableHearingStatementSenderModel.builder()
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
