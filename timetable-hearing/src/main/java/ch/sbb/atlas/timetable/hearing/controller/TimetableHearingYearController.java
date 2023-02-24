package ch.sbb.atlas.timetable.hearing.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.mapper.TimeTableHearingYearMapper;
import ch.sbb.atlas.timetable.hearing.model.TimetableHearingYearSearchRestrictions;
import ch.sbb.atlas.timetable.hearing.service.TimetableHearingYearService;
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
  public TimetableHearingYearModel startHearingYear(TimetableHearingYearModel hearingYearModel) {
    TimetableHearingYear startedHearing = timetableHearingYearService.startTimetableHearing(
        TimeTableHearingYearMapper.toEntity(hearingYearModel));
    return TimeTableHearingYearMapper.toModel(startedHearing);
  }

  @Override
  public TimetableHearingYearModel updateTimetableHearingSettings(TimetableHearingYearModel hearingYearModel) {
    TimetableHearingYear updatedHearing = timetableHearingYearService.updateTimetableHearingSettings(
        TimeTableHearingYearMapper.toEntity(hearingYearModel));
    return TimeTableHearingYearMapper.toModel(updatedHearing);
  }

  @Override
  public TimetableHearingYearModel closeTimetableHearing(TimetableHearingYearModel hearingYearModel) {
    TimetableHearingYear closedHearing = timetableHearingYearService.closeTimetableHearing(
        TimeTableHearingYearMapper.toEntity(hearingYearModel));
    return TimeTableHearingYearMapper.toModel(closedHearing);
  }
}
