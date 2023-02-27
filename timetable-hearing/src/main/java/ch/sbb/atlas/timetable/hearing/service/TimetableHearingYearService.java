package ch.sbb.atlas.timetable.hearing.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.timetable.hearing.exception.HearingCurrentlyActiveException;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
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

  public TimetableHearingYear getHearingYear(Long year) {
    return timetableHearingYearRepository.findById(year).orElseThrow(() -> new IdNotFoundException(year));
  }

  public TimetableHearingYear createTimetableHearing(TimetableHearingYear timetableHearingYear) {
    timetableHearingYear.setHearingStatus(HearingStatus.PLANNED);
    timetableHearingYear.setStatementCreatableExternal(true);
    timetableHearingYear.setStatementCreatableInternal(true);
    timetableHearingYear.setStatementEditable(true);
    return timetableHearingYearRepository.save(timetableHearingYear);
  }

  public TimetableHearingYear startTimetableHearing(TimetableHearingYear timetableHearingYear) {
    mayTransitionToHearingStatus(timetableHearingYear, HearingStatus.ACTIVE);

    timetableHearingYear.setHearingStatus(HearingStatus.ACTIVE);
    timetableHearingYear.setStatementCreatableExternal(true);
    timetableHearingYear.setStatementCreatableInternal(true);
    timetableHearingYear.setStatementEditable(true);
    return timetableHearingYearRepository.save(timetableHearingYear);
  }

  public TimetableHearingYear updateTimetableHearingSettings(TimetableHearingYear timetableHearingYear) {
    return timetableHearingYearRepository.save(timetableHearingYear);
  }

  public TimetableHearingYear closeTimetableHearing(TimetableHearingYear timetableHearingYear) {
    mayTransitionToHearingStatus(timetableHearingYear, HearingStatus.ARCHIVED);

    timetableHearingYear.setHearingStatus(HearingStatus.ARCHIVED);
    return timetableHearingYearRepository.save(timetableHearingYear);
  }

  private void mayTransitionToHearingStatus(TimetableHearingYear timetableHearingYear, HearingStatus hearingStatus) {
    if (hearingStatus == HearingStatus.ACTIVE && timetableHearingYearRepository.hearingActive()) {
      throw new HearingCurrentlyActiveException();
    }
    if (hearingStatus == HearingStatus.ARCHIVED && timetableHearingYear.getHearingStatus() != HearingStatus.ACTIVE) {
      throw new IllegalStateException("Cannot close hearing, since it is not active");
    }
  }

}
