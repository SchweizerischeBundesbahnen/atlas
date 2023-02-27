package ch.sbb.atlas.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.StatementSenderModel;
import ch.sbb.atlas.timetable.hearing.entity.StatementSender;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatementSenderMapper {

  public static StatementSender toEntity(StatementSenderModel statementSenderModel) {
    return StatementSender.builder()
        .firstName(statementSenderModel.getFirstName())
        .lastName(statementSenderModel.getLastName())
        .organisation(statementSenderModel.getOrganisation())
        .street(statementSenderModel.getStreet())
        .zip(statementSenderModel.getZip())
        .city(statementSenderModel.getCity())
        .email(statementSenderModel.getEmail())
        .build();
  }

  public static StatementSenderModel toModel(StatementSender statementSender) {
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
