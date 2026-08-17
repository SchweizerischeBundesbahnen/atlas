package ch.sbb.timetable.hearing.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.timetable.hearing.api.TthYearApiInternal;
import ch.sbb.timetable.hearing.service.YearService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TthYearApiInternalController implements TthYearApiInternal {

  private final YearService yearService;

  @Override
  public TimetableHearingYearModel startTimetableHearingYear(Long year) {
    return yearService.startTimetableHearingYear(year);
  }

  @Override
  public TimetableHearingYearModel closeTimetableHearingYear(Long year) {
    return yearService.closeTimetableHearingYear(year);
  }
}
