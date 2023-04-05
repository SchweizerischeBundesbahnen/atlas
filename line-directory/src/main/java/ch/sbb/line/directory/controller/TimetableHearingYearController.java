package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.mapper.TimeTableHearingYearMapper;
import ch.sbb.line.directory.model.TimetableHearingYearSearchRestrictions;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableHearingYearController implements TimetableHearingYearApiV1 {

  private final TimetableHearingYearService timetableHearingYearService;

  @Override
  public Container<TimetableHearingYearModel> getHearingYears(Pageable pageable, List<HearingStatus> statusChoices) {
    Page<TimetableHearingYear> hearingYears = timetableHearingYearService.getHearingYears(
        TimetableHearingYearSearchRestrictions.builder()
            .pageable(pageable)
            .statusRestrictions(statusChoices)
            .build());
    return Container.<TimetableHearingYearModel>builder()
        .objects(hearingYears.stream().map(TimeTableHearingYearMapper::toModel).toList())
        .totalCount(hearingYears.getTotalElements())
        .build();
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
    TimetableHearingYear updatedHearing = timetableHearingYearService.updateTimetableHearingSettings(year,        TimeTableHearingYearMapper.toEntity(hearingYearModel));
    return TimeTableHearingYearMapper.toModel(updatedHearing);
  }

  @Override
  public TimetableHearingYearModel closeTimetableHearing(Long year) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(year);
    TimetableHearingYear closedHearing = timetableHearingYearService.closeTimetableHearing(hearingYear);
    return TimeTableHearingYearMapper.toModel(closedHearing);
  }
}
