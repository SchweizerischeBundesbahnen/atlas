package ch.sbb.atlas.base.service.imports.servicepoint.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ItemImportResponseStatus {

  SUCCESS,
  FAILED
}
