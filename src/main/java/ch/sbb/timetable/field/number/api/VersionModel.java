package ch.sbb.timetable.field.number.api;

import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.enumaration.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Version")
public class VersionModel {

  @Schema(description = "Technical identifier")
  private Long id;

  @Schema(description = "Timetable field number identifier", example = "ch:1:fpfnid:100000")
  private String ttfnid;

  @Schema(description = "Name")
  private String name;

  @Schema(description = "Number")
  private String number;

  @Schema(description = "Timetable field number")
  private String swissTimetableFieldNumber;

  @Schema(description = "Status")
  private Status status;

  @Schema(description = "Date - valid from")
  private LocalDate validFrom;

  @Schema(description = "Date - valid to")
  private LocalDate validTo;

  @Schema(description = "Business organisation", example = "SBB")
  private String businessOrganisation;

  @Schema(description = "Additional comment")
  private String comment;

  @Schema(description = "Type")
  private Type type;

  @Schema(description = "Compact name")
  private String nameCompact;
}
