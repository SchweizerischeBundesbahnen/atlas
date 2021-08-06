package ch.sbb.timetable.field.number.model;

import java.time.LocalDate;

import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.enumaration.Type;
import io.swagger.v3.oas.annotations.media.Schema;
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

    private String ttfnid;

    private String name;

    private String number;

    private String swissTimetableFieldNumber;

    private Status status;

    private LocalDate validFrom;

    private LocalDate validTo;

    private String businessOrganisation;

    private String comment;

    private Type type;

    private String nameCompact;
}
