package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.FutureTimetableHelper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TimetableFieldNumberResolverServiceTest {

  @Mock
  private TimetableFieldNumberApiInternal timetableFieldNumberApiInternal;

  private TimetableFieldNumberResolverService timetableFieldNumberResolverService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableFieldNumberResolverService = new TimetableFieldNumberResolverService(timetableFieldNumberApiInternal);
  }

  @Test
  void shouldResolveTtfnidNullToNull() {
    String result = timetableFieldNumberResolverService.resolveTtfnid(null);
    assertThat(result).isNull();
  }

  @Test
  void shouldResolveTtfnidBySearchingAtBeginningOfNextTimetableYear() {
    String ttfnid = "ch:1:ttfnid:13132";
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder()
            .objects(List.of(TimetableFieldNumberModel.builder().ttfnid(ttfnid).build()))
            .build());

    String result = timetableFieldNumberResolverService.resolveTtfnid("1.1");
    assertThat(result).isEqualTo(ttfnid);

    verify(timetableFieldNumberApiInternal).getOverview(any(), any(), eq("1.1"), any(),
        eq(FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now())), any());
  }

  @Test
  void shouldResolveAdditionalVersionInfoForEmptyList() {
    List<TimetableHearingStatementModelV2> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(Collections.emptyList());
    assertThat(result).isEmpty();
  }

  @Test
  void shouldResolveAdditionalVersionInfo() {
    // Given
    TimetableFieldNumberVersionModel version = TimetableFieldNumberVersionModel.builder()
        .ttfnid("ch:1:ttfnid:12341241")
        .number("1.1")
        .descriptionOutwardLine1("Bern - Ostermundigen")
        .descriptionReturnLine1("Bern - Ostermundigen")
        .meanOfTransport(TtfnMeanOfTransport.TRAIN)
        .build();
    when(timetableFieldNumberApiInternal.getVersionsValidAt(any(), any())).thenReturn(Collections.singletonList(version));

    TimetableHearingStatementModelV2 statementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:12341241")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    // When
    List<TimetableHearingStatementModelV2> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(Collections.singletonList(statementModel));

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getTimetableFieldNumber()).isEqualTo("1.1");
    assertThat(result.getFirst().getTimetableFieldDescription()).isEqualTo("Bern - Ostermundigen");
  }
}
