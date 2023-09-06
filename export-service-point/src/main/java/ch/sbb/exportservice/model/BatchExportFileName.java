package ch.sbb.exportservice.model;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum BatchExportFileName implements ExportFileName {

  SERVICE_POINT_VERSION("service_point", "service_point"),
  TRAFFIC_POINT_ELEMENT_VERSION("traffic_point", "traffic_point"),
  LOADING_POINT_VERSION("loading_point", "loading_point");

  private final String baseDir;
  private final String fileName;

}
