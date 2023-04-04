package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.ResponsibleTransportCompany;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponsibleTransportCompanyMapper {

  public static TimetableHearingStatementResponsibleTransportCompanyModel toModel(ResponsibleTransportCompany transportCompany) {
    return TimetableHearingStatementResponsibleTransportCompanyModel.builder()
        .id(transportCompany.getTransportCompanyId())
        .number(transportCompany.getNumber())
        .abbreviation(transportCompany.getAbbreviation())
        .businessRegisterName(transportCompany.getBusinessRegisterName())
        .build();
  }

  public static ResponsibleTransportCompany toEntity(
      TimetableHearingStatementResponsibleTransportCompanyModel transportCompanyModel,
      TimetableHearingStatement statement) {
    return ResponsibleTransportCompany.builder()
        .statement(statement)
        .transportCompanyId(transportCompanyModel.getId())
        .number(transportCompanyModel.getNumber())
        .abbreviation(transportCompanyModel.getAbbreviation())
        .businessRegisterName(transportCompanyModel.getBusinessRegisterName())
        .build();
  }

  public static TimetableHearingStatementResponsibleTransportCompanyModel toResponsibleTransportCompany(
      TransportCompanyModel transportCompanyModel) {
    return TimetableHearingStatementResponsibleTransportCompanyModel.builder()
        .id(transportCompanyModel.getId())
        .number(transportCompanyModel.getNumber())
        .abbreviation(transportCompanyModel.getAbbreviation())
        .businessRegisterName(transportCompanyModel.getBusinessRegisterName())
        .build();
  }
}
