package ch.sbb.atlas.export.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExportType implements ExportTypeBase {

  FULL("full", "full_"),
  ACTUAL_DATE("actual_date", "actual_date_"),
  FUTURE_TIMETABLE("future_timetable", "future_timetable_");

  private final String dir;
  private final String fileTypePrefix;

}
