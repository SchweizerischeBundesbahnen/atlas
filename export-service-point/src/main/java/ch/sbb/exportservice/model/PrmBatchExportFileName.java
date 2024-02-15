package ch.sbb.exportservice.model;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum PrmBatchExportFileName implements ExportFileName {

  STOP_POINT_VERSION("stop_point", "stop_point"),
  PLATFORM_VERSION("platform", "platform"),
  REFERENCE_POINT_VERSION("reference_point", "reference_point"),
  CONTACT_POINT_VERSION("contact_point", "contact_point"),
  TOILET_VERSION("toilet", "toilet"),
  PARKING_LOT_VERSION("parking_lot", "parking_lot"),

  ;

  private final String baseDir;
  private final String fileName;

}
