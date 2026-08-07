package ch.sbb.timetable.hearing.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearApiInternal;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.mapper.TimeTableHearingYearMapper;
import ch.sbb.timetable.hearing.model.TimetableHearingYearSearchRestrictions;
import ch.sbb.timetable.hearing.service.TimetableHearingYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimetableHearingYearControllerInternal implements TimetableHearingYearApiInternal {

  private final TimetableHearingYearService timetableHearingYearService;

  @Override
  public List<TimetableHearingYearModel> getHearingYears(List<HearingStatus> statusChoices) {
    List<TimetableHearingYear> hearingYears = timetableHearingYearService.getHearingYears(
        TimetableHearingYearSearchRestrictions.builder()
            .statusRestrictions(statusChoices)
            .build());
    return hearingYears.stream()
        .map(TimeTableHearingYearMapper::toModel)
        .toList();
  }

  @Override
  public TimetableHearingYearModel getHearingYear(Long year) {
    return TimeTableHearingYearMapper.toModel(timetableHearingYearService.getHearingYear(year));
  }

  @Override
  public TimetableHearingYearModel createHearingYear(TimetableHearingYearModel hearingYearModel) {
    TimetableHearingYear newHearing = timetableHearingYearService.createTimetableHearing(
        TimeTableHearingYearMapper.toEntity(hearingYearModel));
    return TimeTableHearingYearMapper.toModel(newHearing);
  }

  @Override
  public TimetableHearingYearModel startHearingYear(Long year) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(year);
    TimetableHearingYear startedHearing = timetableHearingYearService.startTimetableHearing(hearingYear);
    return TimeTableHearingYearMapper.toModel(startedHearing);
  }

  @Override
  public TimetableHearingYearModel updateTimetableHearingSettings(Long year, TimetableHearingYearModel hearingYearModel) {
    TimetableHearingYear updatedHearing = timetableHearingYearService.updateTimetableHearingSettings(year,
        TimeTableHearingYearMapper.toEntity(hearingYearModel));
    return TimeTableHearingYearMapper.toModel(updatedHearing);
  }

  @Override
  public TimetableHearingYearModel closeTimetableHearing(Long year, List<Long> statementIdsToRemoveFromDossier) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(year);
    timetableHearingYearService.mayTransitionToHearingStatus(hearingYear, HearingStatus.ARCHIVED);
    TimetableHearingYear closedHearing = timetableHearingYearService.closeTimetableHearing(hearingYear,
        statementIdsToRemoveFromDossier);
    return TimeTableHearingYearMapper.toModel(closedHearing);
  }
}