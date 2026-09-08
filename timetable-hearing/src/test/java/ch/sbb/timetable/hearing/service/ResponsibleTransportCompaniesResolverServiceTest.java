package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ResponsibleTransportCompaniesResolverServiceTest {

  private static final String TTFNID = "ch:1:ttfnid:12341241";
  private static final String SBOID = "ch:1:sboid:100001";

  @Mock
  private TimetableFieldNumberApiInternal timetableFieldNumberApiInternal;

  @Mock
  private TransportCompanyClient transportCompanyClient;

  private ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    responsibleTransportCompaniesResolverService = new ResponsibleTransportCompaniesResolverService(
        timetableFieldNumberApiInternal, transportCompanyClient);
  }

  @Test
  void shouldReturnEmptyListWhenTtfnidIsNull() {
    List<TransportCompanyModel> result =
        responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(null, LocalDate.now());

    assertThat(result).isEmpty();
    verifyNoInteractions(timetableFieldNumberApiInternal);
    verifyNoInteractions(transportCompanyClient);
  }

  @Test
  void shouldResolveTransportCompaniesForExpiredTimetableFieldNumber() {
    mockOverviewReturning(TimetableFieldNumberModel.builder().ttfnid(TTFNID).businessOrganisation(SBOID).build());
    when(transportCompanyClient.getTransportCompaniesBySboid(SBOID)).thenReturn(
        List.of(TransportCompanyModel.builder().id(1L).number("#0001").build()));

    List<TransportCompanyModel> result =
        responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(TTFNID, LocalDate.now());

    assertThat(result).hasSize(1);
    verify(transportCompanyClient).getTransportCompaniesBySboid(SBOID);
  }

  @Test
  void shouldLookUpTimetableFieldNumberByTtfnidWithoutValidityRestriction() {
    mockOverviewReturning(TimetableFieldNumberModel.builder().ttfnid(TTFNID).businessOrganisation(SBOID).build());

    responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(TTFNID, LocalDate.now());

    ArgumentCaptor<List<String>> ttfnIdsCaptor = ArgumentCaptor.captor();
    verify(timetableFieldNumberApiInternal).getOverview(any(), any(), any(), any(), eq(null), any(),
        ttfnIdsCaptor.capture());
    assertThat(ttfnIdsCaptor.getValue()).containsExactly(TTFNID);
  }

  @Test
  void shouldReturnEmptyListWhenNoTimetableFieldNumberFound() {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder().objects(Collections.emptyList()).build());

    List<TransportCompanyModel> result =
        responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(TTFNID, LocalDate.now());

    assertThat(result).isEmpty();
    verifyNoInteractions(transportCompanyClient);
  }

  @Test
  void shouldReturnEmptyListWhenBusinessOrganisationIsNull() {
    mockOverviewReturning(TimetableFieldNumberModel.builder().ttfnid(TTFNID).build());

    List<TransportCompanyModel> result =
        responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(TTFNID, LocalDate.now());

    assertThat(result).isEmpty();
    verifyNoInteractions(transportCompanyClient);
  }

  @Test
  void shouldMapResponsibleTransportCompanies() {
    mockOverviewReturning(TimetableFieldNumberModel.builder().ttfnid(TTFNID).businessOrganisation(SBOID).build());
    when(transportCompanyClient.getTransportCompaniesBySboid(SBOID)).thenReturn(
        List.of(TransportCompanyModel.builder().id(1L).number("#0001").abbreviation("SBB").build()));

    List<TimetableHearingStatementResponsibleTransportCompanyModel> result =
        responsibleTransportCompaniesResolverService.resolveResponsibleTransportCompanies(TTFNID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getAbbreviation()).isEqualTo("SBB");
  }

  private void mockOverviewReturning(TimetableFieldNumberModel model) {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder().objects(List.of(model)).build());
  }
}
