package ch.sbb.exportservice.model;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum ExportType implements ExportTypeBase {

  SWISS_ONLY_FULL(Constants.FULL_DIR_NAME, Constants.SWISS_ONLY_PREFIX),
  SWISS_ONLY_ACTUAL(Constants.ACTUAL_DATE_DIR_NAME, Constants.SWISS_ONLY_PREFIX),
  SWISS_ONLY_TIMETABLE_FUTURE(Constants.FUTURE_TIMETABLE_DIR_NAME, Constants.SWISS_ONLY_PREFIX),
  WORLD_FULL(Constants.FULL_DIR_NAME, Constants.WORLD_PREFIX),
  WORLD_ONLY_ACTUAL(Constants.ACTUAL_DATE_DIR_NAME, Constants.WORLD_PREFIX),
  WORLD_ONLY_TIMETABLE_FUTURE(Constants.FUTURE_TIMETABLE_DIR_NAME, Constants.WORLD_PREFIX);

  private final String dir;
  private final String fileTypePrefix;

  public static List<ExportType> getWorldOnly() {
    return List.of(WORLD_FULL, WORLD_ONLY_ACTUAL, WORLD_ONLY_TIMETABLE_FUTURE);
  }

  private static class Constants {

    private static final String FULL_DIR_NAME = "full";
    private static final String ACTUAL_DATE_DIR_NAME = "actual_date";
    private static final String FUTURE_TIMETABLE_DIR_NAME = "future_timetable";
    private static final String SWISS_ONLY_PREFIX = "swiss-only";
    private static final String WORLD_PREFIX = "world";

  }
}
