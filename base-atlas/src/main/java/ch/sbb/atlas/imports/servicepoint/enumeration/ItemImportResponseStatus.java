package ch.sbb.atlas.imports.servicepoint.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ItemImportResponseStatus {
  SUCCESS,
  WARNING,
  FAILED
}
