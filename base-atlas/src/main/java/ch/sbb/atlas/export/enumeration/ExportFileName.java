package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public interface ExportFileName {

    @NotNull
    @Schema(description = "")
    String getBaseDir();

    @NotNull
    @Schema(description = "")
    String getFileName();

}
