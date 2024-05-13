package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.line.directory.entity.StatementSender;
import java.util.ArrayList;
import java.util.HashSet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatementSenderMapperV2 {

  public static StatementSender toEntity(TimetableHearingStatementSenderModelV2 timetableHearingStatementSenderModel) {
    return StatementSender.builder()
        .firstName(timetableHearingStatementSenderModel.getFirstName())
        .lastName(timetableHearingStatementSenderModel.getLastName())
        .organisation(timetableHearingStatementSenderModel.getOrganisation())
        .street(timetableHearingStatementSenderModel.getStreet())
        .zip(timetableHearingStatementSenderModel.getZip())
        .city(timetableHearingStatementSenderModel.getCity())
        .emails(new ArrayList<>(timetableHearingStatementSenderModel.getEmails()))
        .build();
  }

  public static TimetableHearingStatementSenderModelV2 toModel(StatementSender statementSender) {
    return TimetableHearingStatementSenderModelV2.builder()
        .firstName(statementSender.getFirstName())
        .lastName(statementSender.getLastName())
        .organisation(statementSender.getOrganisation())
        .street(statementSender.getStreet())
        .zip(statementSender.getZip())
        .city(statementSender.getCity())
        .emails(new HashSet<>(statementSender.getEmails()))
        .build();
  }
}
