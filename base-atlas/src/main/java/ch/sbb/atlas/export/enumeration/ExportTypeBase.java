package ch.sbb.atlas.export.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public interface ExportTypeBase {

    @NotNull
    @Schema(description = "Name of directory e.g. future_timetable, actual_date, full.")
    String getDir();

    @NotNull
    @Schema(description = "Name of prefix type e.g. future_timetable_, actual_date_, full_, swiss-only, world.")
    String getFileTypePrefix();

}
