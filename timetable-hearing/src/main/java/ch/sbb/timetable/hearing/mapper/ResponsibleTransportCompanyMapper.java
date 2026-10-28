package ch.sbb.timetable.hearing.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponsibleTransportCompanyMapper {

  private final TransportCompanyClient transportCompanyClient;

  public TimetableHearingStatementResponsibleTransportCompanyModel toModel(Long transportCompanyId) {
    return toResponsibleTransportCompany(transportCompanyClient.getTransportCompany(transportCompanyId));
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
