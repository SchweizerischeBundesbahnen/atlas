package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public interface ExportTypeBase {

    @NotNull
    @Schema(description = "")
    String getDir();

    @NotNull
    @Schema(description = "")
    String getFileTypePrefix();

}
