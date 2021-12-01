package ch.sbb.timetable.field.number.api;

import ch.sbb.timetable.field.number.enumaration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "TimetableFieldNumber")
public class TimetableFieldNumberModel {

  @Schema(description = "Timetable field number", example = "b0.123")
  @Size(min = 1, max = 50)
  @NotNull
  private String swissTimetableFieldNumber;

  @Schema(description = "Timetable field number identifier", example = "ch:1:fpfnid:100000")
  @Size(min = 1, max = 500)
  @NotNull
  private String ttfnid;

  @Schema(description = "Name", example = "Fribourg/Freiburg - Bern - Thun (S-Bahn Bern, Linien S1, S2)")
  @Size(max = 255)
  private String name;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Status status;

  @Schema(description = "Date - valid from", example = "2021-11-23")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Date - valid to", example = "2021-12-01")
  @NotNull
  private LocalDate validTo;

}
