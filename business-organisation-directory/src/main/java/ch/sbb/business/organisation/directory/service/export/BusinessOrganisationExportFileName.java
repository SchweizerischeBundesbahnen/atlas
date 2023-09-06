package ch.sbb.business.organisation.directory.service.export;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum BusinessOrganisationExportFileName implements ExportFileName {

  BUSINESS_ORGANISATION_VERSION("business_organisation", "business_organisation_versions");

  private final String baseDir;
  private final String fileName;

}
