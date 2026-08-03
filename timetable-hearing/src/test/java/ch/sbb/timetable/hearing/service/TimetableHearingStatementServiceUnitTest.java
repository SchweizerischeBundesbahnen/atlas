package ch.sbb.timetable.hearing.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.timetable.hearing.repository.TimetableHearingStatementRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimetableHearingStatementServiceUnitTest {

  @Mock
  private TimetableHearingStatementRepository timetableHearingStatementRepository;

  @InjectMocks
  private TimetableHearingStatementService timetableHearingStatementService;

  @Test
  void shouldCallRepositoryOnDeleteSpamMailFromYear() {
    // given
    long year = 2022;
    // when
    timetableHearingStatementService.deleteSpamMailFromYear(year);
    // then
    verify(timetableHearingStatementRepository).deleteByStatementStatusAndTimetableYear(StatementStatus.JUNK, year);
  }

  @Test
  void shouldCallRepositoryOnRemoveDossierRelations() {
    // when
    timetableHearingStatementService.removeDossierRelationsAndStatusToReceivedFor(List.of(1L, 2L, 3L));
    // then
    verify(timetableHearingStatementRepository).removeDossierRelationAndSetReceivedFor(List.of(1L, 2L, 3L));
  }
}