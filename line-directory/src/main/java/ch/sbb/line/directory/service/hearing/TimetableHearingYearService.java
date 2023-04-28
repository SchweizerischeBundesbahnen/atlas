package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.exception.HearingCurrentlyActiveException;
import ch.sbb.line.directory.exception.NoHearingCurrentlyActiveException;
import ch.sbb.line.directory.model.TimetableHearingYearSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingYearService {

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingStatementService timetableHearingStatementService;

  public Page<TimetableHearingYear> getHearingYears(TimetableHearingYearSearchRestrictions searchRestrictions) {
    return timetableHearingYearRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public TimetableHearingYear getActiveHearingYear() {
    TimetableHearingYearSearchRestrictions searchRestrictions =
        TimetableHearingYearSearchRestrictions.builder().
            statusRestrictions(Set.of(HearingStatus.ACTIVE))
            .build();
    return timetableHearingYearRepository.findAll(searchRestrictions.getSpecification()).stream().findFirst()
        .orElseThrow(NoHearingCurrentlyActiveException::new);
  }

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

  public TimetableHearingYear updateTimetableHearingSettings(Long year, TimetableHearingYear timetableHearingYear) {
    TimetableHearingYear hearingYear = getHearingYear(year);
    hearingYear.setStatementEditable(timetableHearingYear.isStatementEditable());
    hearingYear.setStatementCreatableInternal(timetableHearingYear.isStatementCreatableInternal());
    hearingYear.setStatementCreatableExternal(timetableHearingYear.isStatementCreatableExternal());
    return hearingYear;
  }

  public TimetableHearingYear closeTimetableHearing(TimetableHearingYear timetableHearingYear) {
    mayTransitionToHearingStatus(timetableHearingYear, HearingStatus.ARCHIVED);

    timetableHearingStatementService.deleteSpamMailFromYear(timetableHearingYear.getTimetableYear());

    timetableHearingStatementService.moveClosedStatementsToNextYearWithStatusUpdates(timetableHearingYear.getTimetableYear());

    timetableHearingYear.setStatementCreatableInternal(false);
    timetableHearingYear.setStatementCreatableExternal(false);
    timetableHearingYear.setStatementEditable(false);

    timetableHearingYear.setHearingStatus(HearingStatus.ARCHIVED);
    return timetableHearingYearRepository.save(timetableHearingYear);
  }

  private void mayTransitionToHearingStatus(TimetableHearingYear timetableHearingYear, HearingStatus hearingStatus) {
    if (hearingStatus == HearingStatus.ACTIVE) {
      if (timetableHearingYearRepository.hearingActive()) {
        throw new HearingCurrentlyActiveException();
      }
      if (timetableHearingYear.getHearingStatus() != HearingStatus.PLANNED) {
        throw new IllegalStateException(
            "May not transition from " + timetableHearingYear.getHearingStatus() + " to " + HearingStatus.PLANNED);
      }
    }
    if (hearingStatus == HearingStatus.ARCHIVED && timetableHearingYear.getHearingStatus() != HearingStatus.ACTIVE) {
      throw new IllegalStateException("Cannot close hearing, since it is not active");
    }
  }

}
