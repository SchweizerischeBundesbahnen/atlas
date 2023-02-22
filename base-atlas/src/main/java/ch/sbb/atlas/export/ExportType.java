package ch.sbb.atlas.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExportType {

  FULL("full_"),
  ACTUAL_DATE("actual_date_"),
  FUTURE_TIMETABLE("future_timetable_");

  private final String filePrefix;
}
