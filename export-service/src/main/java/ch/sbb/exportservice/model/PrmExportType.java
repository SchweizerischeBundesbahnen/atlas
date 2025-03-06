package ch.sbb.exportservice.model;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public enum PrmExportType implements ExportTypeBase {

  FULL(Constants.FULL_DIR_NAME, ""),
  ACTUAL(Constants.ACTUAL_DATE_DIR_NAME, ""),
  TIMETABLE_FUTURE(Constants.FUTURE_TIMETABLE_DIR_NAME, "");

  private final String dir;
  private final String fileTypePrefix;

}
