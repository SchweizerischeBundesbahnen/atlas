package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum BoDiBatchExportFileName implements ExportFileName {

  TRANSPORT_COMPANY("transport_company", "transport_company"),
  BUSINESS_ORGANISATION_VERSION("business_organisation", "business_organisation_versions"); // todo: not stream already (remove
  // and check that v1 still works)

  private final String baseDir;
  private final String fileName;

}
