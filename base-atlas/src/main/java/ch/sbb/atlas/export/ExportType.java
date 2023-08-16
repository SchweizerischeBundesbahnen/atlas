package ch.sbb.atlas.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExportType {

  FULL("full_", "full"),
  ACTUAL_DATE("actual_date_", "actual_date"),
  FUTURE_TIMETABLE("future_timetable_", "future_timetable");

  private final String filePrefix;
  private final String dir;
}
