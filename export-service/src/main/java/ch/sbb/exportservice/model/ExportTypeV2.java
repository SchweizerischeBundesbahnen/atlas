package ch.sbb.exportservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportTypeV2 {

  ACTUAL(Constants.ACTUAL_DATE, ""),
  FULL("full", ""),
  FUTURE_TIMETABLE(Constants.FUTURE_TIMETABLE, ""),
  TIMETABLE_YEARS("timetable-years", ""),

  SWISS_ACTUAL(Constants.ACTUAL_DATE, Constants.SWISS),
  SWISS_FULL("full", Constants.SWISS),
  SWISS_FUTURE_TIMETABLE(Constants.FUTURE_TIMETABLE, Constants.SWISS),

  WORLD_ACTUAL(Constants.ACTUAL_DATE, Constants.WORLD),
  WORLD_FULL("full", Constants.WORLD),
  WORLD_FUTURE_TIMETABLE(Constants.FUTURE_TIMETABLE, Constants.WORLD);

  final String dir;
  final String prefix;

  private static class Constants {

    private static final String ACTUAL_DATE = "actual-date";
    private static final String FUTURE_TIMETABLE = "future-timetable";
    private static final String SWISS = "swiss";
    private static final String WORLD = "world";
  }
}
