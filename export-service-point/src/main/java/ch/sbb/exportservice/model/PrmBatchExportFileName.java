package ch.sbb.exportservice.model;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum PrmBatchExportFileName implements ExportFileName {

  STOP_POINT_VERSION("stop-point", "stop-point" ),
  PLATFORM_VERSION("platform", "platform"),
  REFERENCE_POINT_VERSION("reference-point", "reference-point" );

  private final String baseDir;
  private final String fileName;

}
