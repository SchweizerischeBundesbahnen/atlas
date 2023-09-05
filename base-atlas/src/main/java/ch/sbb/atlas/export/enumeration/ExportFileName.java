package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public interface ExportFileName {

  @NotNull
  @Schema(description = "Name of directory e.g. business_organisation, service_point, traffic_point.")
  String getBaseDir();

  @NotNull
  @Schema(description = "Name of file e.g. business_organisation_versions, service_point, traffic_point.")
  String getFileName();

}
