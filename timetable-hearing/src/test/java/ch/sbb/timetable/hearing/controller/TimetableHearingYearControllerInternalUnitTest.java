package ch.sbb.timetable.hearing.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.service.DossierService;
import ch.sbb.timetable.hearing.service.TimetableHearingStatementService;
import ch.sbb.timetable.hearing.service.TimetableHearingYearService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimetableHearingYearControllerInternalUnitTest {

  @Mock
  private TimetableHearingYearService timetableHearingYearService;
  @Mock
  private TimetableHearingStatementService timetableHearingStatementService;
  @Mock
  private DossierService dossierService;

  @InjectMocks
  private TimetableHearingYearControllerInternal timetableHearingYearControllerInternal;

  @Test
  void shouldCloseTimetableHearingCorrectly() {
    // given
    TimetableHearingYear tthYearActive = TimetableHearingYear.builder().hearingStatus(HearingStatus.ACTIVE).build();
    TimetableHearingYear tthYearClosed = TimetableHearingYear.builder().hearingStatus(HearingStatus.ARCHIVED).build();
    when(dossierService.getStatementIdsFromDossierStatus(anyList())).thenReturn(List.of(1L, 3L, 5L));
    when(timetableHearingYearService.getHearingYear(anyLong())).thenReturn(tthYearActive);
    doNothing().when(timetableHearingYearService)
        .mayTransitionToHearingStatus(any(TimetableHearingYear.class), any(HearingStatus.class));
    when(timetableHearingYearService.closeTimetableHearing(any(TimetableHearingYear.class), anyList())).thenReturn(tthYearClosed);
    // when
    TimetableHearingYearModel closedHearingYear = timetableHearingYearControllerInternal.closeTimetableHearing(2026L, null);
    // then
    assertThat(closedHearingYear.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);
    verify(dossierService).updateDossierStatusClosingYear();
    verify(timetableHearingYearService).getHearingYear(2026L);
    verify(timetableHearingYearService).mayTransitionToHearingStatus(tthYearActive, HearingStatus.ARCHIVED);
    verify(timetableHearingYearService).closeTimetableHearing(tthYearActive, List.of(1L, 3L, 5L));
  }
}