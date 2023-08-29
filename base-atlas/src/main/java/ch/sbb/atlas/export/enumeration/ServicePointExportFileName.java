package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum ServicePointExportFileName implements ExportFileName {

        SERVICE_POINT_VERSION("service_point","service_point"),
        TRAFFIC_POINT_ELEMENT_VERSION("traffic_point","traffic_point");

        private final String baseDir;
        private final String fileName;

}
