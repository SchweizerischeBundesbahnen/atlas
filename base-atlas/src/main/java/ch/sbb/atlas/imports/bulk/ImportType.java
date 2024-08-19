package ch.sbb.atlas.imports.bulk;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ImportType {

  CREATE,
  UPDATE,
  TERMINATE,

  ;

}
