package ch.sbb.line.directory.service.hearing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;

class TimetableFieldNumberResolverServiceTest {

  @Mock
  private TimetableFieldNumberService timetableFieldNumberService;

  private TimetableFieldNumberResolverService timetableFieldNumberResolverService;

  @Captor
  private ArgumentCaptor<TimetableFieldNumberSearchRestrictions> searchRestrictionsArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableFieldNumberResolverService = new TimetableFieldNumberResolverService(timetableFieldNumberService);
  }

  @Test
  void shouldResolveTtfnidNullToNull() {
    String result = timetableFieldNumberResolverService.resolveTtfnid(null);
    assertThat(result).isNull();
  }

  @Test
  void shouldResolveTtfnidBySearchingAtBeginningOfNextTimetableYear() {
    String ttfnid = "ch:1:ttfnid:13132";
    when(timetableFieldNumberService.getVersionsSearched(any())).thenReturn(new PageImpl<>(List.of(TimetableFieldNumber.builder()
        .ttfnid(ttfnid).build())));

    String result = timetableFieldNumberResolverService.resolveTtfnid("1.1");
    assertThat(result).isEqualTo(ttfnid);

    verify(timetableFieldNumberService).getVersionsSearched(searchRestrictionsArgumentCaptor.capture());
    TimetableFieldNumberSearchRestrictions appliedSearchRestrictions = searchRestrictionsArgumentCaptor.getValue();
    assertThat(appliedSearchRestrictions.getNumber()).isEqualTo("1.1");
    assertThat(appliedSearchRestrictions.getValidOn().orElseThrow()).isEqualTo(
        FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now()));
  }

  @Test
  void shouldResolveAdditionalVersionInfoForEmptyList() {
    List<TimetableHearingStatementModel> result = timetableFieldNumberResolverService.resolveAdditionalVersionInfo(Collections.emptyList());
    assertThat(result).isEmpty();
  }

  @Test
  void shouldResolveAdditionalVersionInfo() {
    // Given
    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:12341241")
        .number("1.1")
        .description("Bern - Ostermundigen")
        .build();
    when(timetableFieldNumberService.getVersionsValidAt(any(), any())).thenReturn(Collections.singletonList(version));

    TimetableHearingStatementModel statementModel =TimetableHearingStatementModel.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:12341241")
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    // When
    List<TimetableHearingStatementModel> result =
        timetableFieldNumberResolverService.resolveAdditionalVersionInfo(Collections.singletonList(statementModel));

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTimetableFieldNumber()).isEqualTo("1.1");
    assertThat(result.get(0).getTimetableFieldDescription()).isEqualTo("Bern - Ostermundigen");
  }
}