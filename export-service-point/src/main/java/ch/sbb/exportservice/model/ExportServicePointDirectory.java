package ch.sbb.exportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExportServicePointDirectory {
  FULL("full"),
  ACTUAL_DATE("actual_date"),
  FUTURE_TIMETABLE("future_timetable");

  private final String subDir;
}
