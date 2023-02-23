package ch.sbb.atlas.timetable.hearing.service;

import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.timetable.hearing.exception.HearingCurrentlyActiveException;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingYearService {

  private final TimetableHearingYearRepository timetableHearingYearRepository;

  public TimetableHearingYear createTimetableHearing(Long year, LocalDate hearingFrom, LocalDate hearingTo) {
    return timetableHearingYearRepository.save(TimetableHearingYear.builder()
        .timetableYear(year)
        .hearingFrom(hearingFrom)
        .hearingTo(hearingTo)
        .hearingStatus(HearingStatus.PLANNED)
        .statementCreatableExternal(true)
        .statementCreatableInternal(true)
        .statementEditable(true)
        .build());
  }

  // Attention: concurrent-update, check it
  public void startTimetableHearing(TimetableHearingYear hearing) {
    if (timetableHearingYearRepository.hearingActive()) {
      throw new HearingCurrentlyActiveException();
    }

    hearing.setHearingStatus(HearingStatus.ACTIVE);
    hearing.setStatementCreatableExternal(true);
    hearing.setStatementCreatableInternal(true);
    hearing.setStatementEditable(true);
  }

  // Attention: concurrent-update, check it
  public TimetableHearingYear updateTimetableHearingSettings(TimetableHearingYear hearing) {
    // Settings from model
    hearing.setStatementCreatableExternal(false);
    hearing.setStatementCreatableInternal(false);
    hearing.setStatementEditable(false);
    return hearing;
  }

  // Attention: concurrent-update, check it
  public void closeTimetableHearing(TimetableHearingYear hearing) {
    hearing.setHearingStatus(HearingStatus.ARCHIVED);
  }

}
