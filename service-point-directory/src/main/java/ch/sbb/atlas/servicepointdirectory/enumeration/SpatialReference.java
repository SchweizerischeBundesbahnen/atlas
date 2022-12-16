package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum SpatialReference {

  WGS84WEB,
  LV95,
  LV03,
  WGS84,

  ;
}
