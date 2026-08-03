package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.exception.HearingCurrentlyActiveException;
import ch.sbb.timetable.hearing.repository.TimetableHearingYearRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimetableHearingYearServiceUnitTest {

  @InjectMocks
  private TimetableHearingYearService timetableHearingYearService;

  @Mock
  private TimetableHearingYearRepository timetableHearingYearRepository;
  @Mock
  private TimetableHearingStatementService timetableHearingStatementService;

  @Test
  void shouldCloseTimetableHearingCorrectly() {
    // given
    TimetableHearingYear tthYear = TimetableHearingYear.builder()
        .timetableYear(2026L)
        .build();
    doNothing().when(timetableHearingStatementService).deleteSpamMailFromYear(anyLong());
    doNothing().when(timetableHearingStatementService).removeDossierRelationsAndStatusToReceivedFor(anyList());
    doNothing().when(timetableHearingStatementService).moveClosedStatementsToNextYearWithStatusUpdates(anyLong());
    when(timetableHearingYearRepository.save(any(TimetableHearingYear.class))).thenReturn(null);
    // when
    timetableHearingYearService.closeTimetableHearing(tthYear, List.of(1L, 3L, 5L));
    // then
    verify(timetableHearingStatementService).deleteSpamMailFromYear(2026L);
    verify(timetableHearingStatementService).removeDossierRelationsAndStatusToReceivedFor(List.of(1L, 3L, 5L));
    verify(timetableHearingStatementService).moveClosedStatementsToNextYearWithStatusUpdates(2026L);
    verify(timetableHearingYearRepository).save(assertArg(arg -> {
      assertThat(arg.isStatementCreatableInternal()).isFalse();
      assertThat(arg.isStatementCreatableExternal()).isFalse();
      assertThat(arg.isStatementEditable()).isFalse();
      assertThat(arg.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);
      assertThat(arg.getTimetableYear()).isEqualTo(2026);
    }));
  }

  @Test
  void shouldThrowWhenTransitionToActiveWhenCurrentlyActive() {
    // given
    when(timetableHearingYearRepository.hearingActive()).thenReturn(true);
    // when
    assertThrows(HearingCurrentlyActiveException.class, () -> timetableHearingYearService.mayTransitionToHearingStatus(null,
        HearingStatus.ACTIVE));
  }

  @Test
  void shouldThrowWhenTransitionToActiveWhenNotCurrentlyActiveAndYearNotPlanned() {
    // given
    TimetableHearingYear activeYear = TimetableHearingYear.builder()
        .hearingStatus(HearingStatus.ACTIVE)
        .build();
    when(timetableHearingYearRepository.hearingActive()).thenReturn(false);
    // when
    assertThrows(IllegalStateException.class,
        () -> timetableHearingYearService.mayTransitionToHearingStatus(activeYear, HearingStatus.ACTIVE));
  }

  @Test
  void shouldThrowWhenTransitionToArchivedWhenYearNotActive() {
    // given
    TimetableHearingYear plannedYear = TimetableHearingYear.builder()
        .hearingStatus(HearingStatus.PLANNED)
        .build();
    // when
    assertThrows(IllegalStateException.class,
        () -> timetableHearingYearService.mayTransitionToHearingStatus(plannedYear, HearingStatus.ARCHIVED));
  }
}