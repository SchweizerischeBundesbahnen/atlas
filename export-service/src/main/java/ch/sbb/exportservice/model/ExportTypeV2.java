package ch.sbb.exportservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportTypeV2 {

  ACTUAL,
  FULL,
  FUTURE_TIMETABLE,

  SWISS_ACTUAL,
  SWISS_FULL,
  SWISS_FUTURE_TIMETABLE,

  WORLD_ACTUAL,
  WORLD_FULL,
  WORLD_FUTURE_TIMETABLE,

}
