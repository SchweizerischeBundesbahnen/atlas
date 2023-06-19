package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.SharedTransportCompany;
import ch.sbb.line.directory.repository.SharedTransportCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponsibleTransportCompanyMapper {

  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;

  public static TimetableHearingStatementResponsibleTransportCompanyModel toModel(SharedTransportCompany transportCompany) {
    return TimetableHearingStatementResponsibleTransportCompanyModel.builder()
        .id(transportCompany.getId())
        .number(transportCompany.getNumber())
        .abbreviation(transportCompany.getAbbreviation())
        .businessRegisterName(transportCompany.getBusinessRegisterName())
        .build();
  }

  public SharedTransportCompany toEntity(TimetableHearingStatementResponsibleTransportCompanyModel transportCompanyModel) {
    return sharedTransportCompanyRepository.findById(transportCompanyModel.getId()).orElseThrow();
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
