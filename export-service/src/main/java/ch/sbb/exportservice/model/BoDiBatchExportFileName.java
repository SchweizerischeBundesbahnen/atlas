package ch.sbb.exportservice.model;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum BoDiBatchExportFileName implements ExportFileName {

  TRANSPORT_COMPANY("transport_company", "transport_company"),

  ;

  private final String baseDir;
  private final String fileName;

}
