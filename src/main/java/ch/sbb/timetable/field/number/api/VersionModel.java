package ch.sbb.timetable.field.number.api;

import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Version")
public class VersionModel implements DatesValidator {

  @Schema(description = "Technical identifier")
  private Long id;

  @Schema(description = "Timetable field number identifier", example = "ch:1:ttfnid:100000", accessMode = AccessMode.READ_ONLY)
  private String ttfnid;

  @Schema(description = "Name", example = "Fribourg/Freiburg - Bern - Thun (S-Bahn Bern, Linien S1, S2)")
  @Size(max = 255)
  private String name;

  @Schema(description = "Number", example = "100; 80.099; 2700")
  @Size(min = 1, max = 50)
  @NotNull
  @Pattern(regexp = "^[.0-9]{1,50}$")
  private String number;

  @Schema(description = "Timetable field number")
  @Size(min = 1, max = 50)
  @NotNull
  private String swissTimetableFieldNumber;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "Date - valid from", example = "2021-11-23")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Date - valid to", example = "2021-12-01")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "Business organisation", example = "11 - SBB - Schweizerische Bundesbahnen - 100001")
  @Size(min = 1, max = 50)
  @NotNull
  private String businessOrganisation;

  @Schema(description = "Additional comment", example = "Hier kann für interne Zwecke ein Kommentar welcher das Fahrplanfeld betrifft erfasst werden.")
  @Size(max = 250)
  private String comment;
}
