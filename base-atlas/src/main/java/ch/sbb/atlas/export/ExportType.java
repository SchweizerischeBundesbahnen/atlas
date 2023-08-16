package ch.sbb.atlas.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ExportType {

  FULL("full_", "full", "full"),
  ACTUAL_DATE("actual_date_", "actual_date", "actual_date"),
  FUTURE_TIMETABLE("future_timetable_", "future_timetable", "future_timetable");

  private final String filePrefix;
  private final String dir;
  private final String name;

  public static List<String> getExportTypeNames() {
    List<String> exportTypeNames = new ArrayList<>();
    exportTypeNames.add(ExportType.FULL.name);
    exportTypeNames.add(ExportType.ACTUAL_DATE.name);
    exportTypeNames.add(ExportType.FUTURE_TIMETABLE.name);
    return exportTypeNames;
  }

}
