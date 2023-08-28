package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum BoExportFileName implements ExportFileName {

        BUSINESS_ORGANISATION_VERSION("business_organisation","business_organisation_versions");

        private final String baseDir;
        private final String fileName;

}
