package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum SpatialReference {

  WGS84WEB(3857),
  LV95(2056),
  LV03(21781),
  WGS84(4326),

  ;

  private final Integer wkid;
}
