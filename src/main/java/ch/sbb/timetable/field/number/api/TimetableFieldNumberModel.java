package ch.sbb.timetable.field.number.api;

import ch.sbb.timetable.field.number.enumaration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

  @Schema(description = "Timetable field number")
  private String swissTimetableFieldNumber;

  @Schema(description = "Timetable field number identifier", example = "ch:1:fpfnid:100000")
  private String ttfnid;

  @Schema(description = "Name")
  private String name;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Schema(description = "Date - valid from")
  private LocalDate validFrom;

  @Schema(description = "Date - valid to")
  private LocalDate validTo;

}
