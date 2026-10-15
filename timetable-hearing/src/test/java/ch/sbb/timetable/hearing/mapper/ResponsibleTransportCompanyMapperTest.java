package ch.sbb.timetable.hearing.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import org.junit.jupiter.api.Test;

class ResponsibleTransportCompanyMapperTest {

  @Test
  void shouldResolveTransportCompanyFromBodi() {
    TransportCompanyClient transportCompanyClient = mock(TransportCompanyClient.class);
    TransportCompanyModel transportCompany = TransportCompanyModel.builder()
        .id(1L)
        .number("#0001")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .build();
    when(transportCompanyClient.getTransportCompany(1L)).thenReturn(transportCompany);

    TimetableHearingStatementResponsibleTransportCompanyModel result =
        new ResponsibleTransportCompanyMapper(transportCompanyClient).toModel(1L);

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
            .id(1L)
            .number("#0001")
            .abbreviation("SBB")
            .businessRegisterName("Schweizerische Bundesbahnen SBB")
            .build());
    verify(transportCompanyClient).getTransportCompany(1L);
  }
}
