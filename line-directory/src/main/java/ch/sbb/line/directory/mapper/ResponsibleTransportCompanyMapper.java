package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import ch.sbb.atlas.transport.company.service.SharedTransportCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponsibleTransportCompanyMapper {

  private final SharedTransportCompanyService sharedTransportCompanyService;

  public static TimetableHearingStatementResponsibleTransportCompanyModel toModel(SharedTransportCompany transportCompany) {
    return TimetableHearingStatementResponsibleTransportCompanyModel.builder()
        .id(transportCompany.getId())
        .number(transportCompany.getNumber())
        .abbreviation(transportCompany.getAbbreviation())
        .businessRegisterName(transportCompany.getBusinessRegisterName())
        .build();
  }

  public SharedTransportCompany toEntity(TimetableHearingStatementResponsibleTransportCompanyModel transportCompanyModel) {
    return sharedTransportCompanyService.findById(transportCompanyModel.getId()).orElseThrow();
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
