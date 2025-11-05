package ch.sbb.exportservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportTypeV2 {

  ACTUAL(Constants.ACTUAL_DATE, ""),
  FULL(Constants.FULL, ""),

  // future-timetable export will be deleted on 01.05.2026
  FUTURE_TIMETABLE(Constants.FUTURE_TIMETABLE, ""),

  TIMETABLE_YEARS(Constants.TIMETABLE_YEARS, ""),

  SWISS_ACTUAL(Constants.ACTUAL_DATE, Constants.SWISS),
  SWISS_FULL(Constants.FULL, Constants.SWISS),
  SWISS_FUTURE_TIMETABLE(Constants.FUTURE_TIMETABLE, Constants.SWISS),
  SWISS_TIMETABLE_YEARS(Constants.TIMETABLE_YEARS, Constants.SWISS),

  WORLD_ACTUAL(Constants.ACTUAL_DATE, Constants.WORLD),
  WORLD_FULL(Constants.FULL, Constants.WORLD),
  WORLD_FUTURE_TIMETABLE(Constants.FUTURE_TIMETABLE, Constants.WORLD),
  WORLD_TIMETABLE_YEARS(Constants.TIMETABLE_YEARS, Constants.WORLD);

  final String dir;
  final String prefix;

  private static class Constants {

    private static final String ACTUAL_DATE = "actual-date";
    private static final String FUTURE_TIMETABLE = "future-timetable";
    private static final String TIMETABLE_YEARS = "timetable-years";
    private static final String SWISS = "swiss";
    private static final String WORLD = "world";
    private static final String FULL = "full";
  }
}
