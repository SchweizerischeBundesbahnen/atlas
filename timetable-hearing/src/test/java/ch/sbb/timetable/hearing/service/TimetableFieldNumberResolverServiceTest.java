package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.kafka.model.SwissCanton;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TimetableFieldNumberResolverServiceTest {

  private static final String TTFNID = "ch:1:ttfnid:12341241";
  private static final String OTHER_TTFNID = "ch:1:ttfnid:99999999";

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
    verifyNoInteractions(timetableFieldNumberApiInternal);
  }

  @Test
  void shouldResolveTtfnidWithoutValidityRestriction() {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder()
            .objects(List.of(TimetableFieldNumberModel.builder().ttfnid(TTFNID).build()))
            .build());

    String result = timetableFieldNumberResolverService.resolveTtfnid("1.1");

    assertThat(result).isEqualTo(TTFNID);
    verify(timetableFieldNumberApiInternal).getOverview(any(), any(), eq("1.1"), isNull(), isNull(), any(), any(), isNull());
  }

  @Test
  void shouldResolveTtfnidWhenAllVersionsExpired() {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder()
            .objects(List.of(TimetableFieldNumberModel.builder().ttfnid(TTFNID).build()))
            .build());

    assertThat(timetableFieldNumberResolverService.resolveTtfnid("1.1")).isEqualTo(TTFNID);
  }

  @Test
  void shouldNotResolveTtfnidWhenNumberIsAmbiguous() {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder()
            .objects(List.of(TimetableFieldNumberModel.builder().ttfnid(TTFNID).build(),
                TimetableFieldNumberModel.builder().ttfnid(OTHER_TTFNID).build()))
            .build());

    assertThat(timetableFieldNumberResolverService.resolveTtfnid("1.1")).isNull();
  }

  @Test
  void shouldResolveAdditionalVersionInfoForEmptyList() {
    List<TimetableHearingStatementModelV2> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(Collections.emptyList());

    assertThat(result).isEmpty();
    verifyNoInteractions(timetableFieldNumberApiInternal);
  }

  @Test
  void shouldResolveAdditionalVersionInfo() {
    mockOverviewFor(TTFNID, "1.1", "Bern - Ostermundigen");

    List<TimetableHearingStatementModelV2> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(List.of(statement(TTFNID, 2023L)));

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getTimetableFieldNumber()).isEqualTo("1.1");
    assertThat(result.getFirst().getTimetableFieldDescription()).isEqualTo("Bern - Ostermundigen");
  }

  @Test
  void shouldResolveAdditionalVersionInfoForStatementsOfDifferentTimetableYears() {
    mockOverviewFor(TTFNID, "1.1", "Bern - Ostermundigen");

    List<TimetableHearingStatementModelV2> result = timetableFieldNumberResolverService.resolveAdditionalVersionInfo(
        List.of(statement(TTFNID, 2023L), statement(TTFNID, 2024L)));

    assertThat(result).hasSize(2);
    assertThat(result).allSatisfy(statement -> assertThat(statement.getTimetableFieldNumber()).isEqualTo("1.1"));
  }

  @Test
  void shouldResolveAdditionalVersionInfoWithSingleApiCallForDistinctTtfnids() {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder().objects(Collections.emptyList()).build());

    timetableFieldNumberResolverService.resolveAdditionalVersionInfo(
        List.of(statement(TTFNID, 2023L), statement(TTFNID, 2023L), statement(OTHER_TTFNID, 2023L)));

    ArgumentCaptor<List<String>> ttfnIdsCaptor = ArgumentCaptor.captor();
    verify(timetableFieldNumberApiInternal, times(1)).getOverview(any(), any(), any(), any(), any(), any(),
        ttfnIdsCaptor.capture(), isNull());
    assertThat(ttfnIdsCaptor.getValue()).containsExactly(TTFNID, OTHER_TTFNID);
  }

  @Test
  void shouldKeepDisplayFieldsNullWhenStatementHasNoTtfnid() {
    List<TimetableHearingStatementModelV2> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(List.of(statement(null, 2023L)));

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getTimetableFieldNumber()).isNull();
    assertThat(result.getFirst().getTimetableFieldDescription()).isNull();
    verify(timetableFieldNumberApiInternal, never()).getOverview(any(), any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  void shouldKeepDisplayFieldsNullWhenApiReturnsNoRow() {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder().objects(Collections.emptyList()).build());

    List<TimetableHearingStatementModelV2> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(List.of(statement(TTFNID, 2023L)));

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getTimetableFieldNumber()).isNull();
  }

  private void mockOverviewFor(String ttfnid, String number, String description) {
    when(timetableFieldNumberApiInternal.getOverview(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder()
            .objects(List.of(TimetableFieldNumberModel.builder()
                .ttfnid(ttfnid)
                .number(number)
                .descriptionOutwardLine1(description)
                .build()))
            .build());
  }

  private TimetableHearingStatementModelV2 statement(String ttfnid, Long timetableYear) {
    return TimetableHearingStatementModelV2.builder()
        .timetableYear(timetableYear)
        .swissCanton(SwissCanton.BERN)
        .ttfnid(ttfnid)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
  }
}
