package ch.sbb.exportservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportTypeV2 {

  ACTUAL("actual-date", ""),
  FULL("full", ""),
  FUTURE_TIMETABLE("future-timetable", ""),
  TIMETABLE_YEARS("timetable-years", ""),

  SWISS_ACTUAL("actual-date", "swiss"),
  SWISS_FULL("full", "swiss"),
  SWISS_FUTURE_TIMETABLE("future-timetable", "swiss"),

  WORLD_ACTUAL("actual-date", "world"),
  WORLD_FULL("full", "world"),
  WORLD_FUTURE_TIMETABLE("future-timetable", "world");

  final String dir;
  final String prefix;

}
