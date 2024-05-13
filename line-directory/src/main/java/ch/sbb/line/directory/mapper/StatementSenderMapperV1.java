package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV1;
import ch.sbb.line.directory.entity.StatementSender;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatementSenderMapperV1 {

  public static StatementSender toEntity(TimetableHearingStatementSenderModelV1 timetableHearingStatementSenderModel) {
    return StatementSender.builder()
        .firstName(timetableHearingStatementSenderModel.getFirstName())
        .lastName(timetableHearingStatementSenderModel.getLastName())
        .organisation(timetableHearingStatementSenderModel.getOrganisation())
        .street(timetableHearingStatementSenderModel.getStreet())
        .zip(timetableHearingStatementSenderModel.getZip())
        .city(timetableHearingStatementSenderModel.getCity())
        .emails(List.of(timetableHearingStatementSenderModel.getEmail()))
        .build();
  }

  public static TimetableHearingStatementSenderModelV1 toModel(StatementSender statementSender) {
    return TimetableHearingStatementSenderModelV1.builder()
        .firstName(statementSender.getFirstName())
        .lastName(statementSender.getLastName())
        .organisation(statementSender.getOrganisation())
        .street(statementSender.getStreet())
        .zip(statementSender.getZip())
        .city(statementSender.getCity())
        .email(statementSender.getEmails().getFirst())
        .build();
  }
}
