package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class TimetableHearingStatementSenderModel {

  @Schema(description = "First Name", example = "Fabienne")
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String firstName;

  @Schema(description = "Last Name", example = "Maurer")
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String lastName;

  @Schema(description = "Organisation", example = "Post AG")
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String organisation;

  @Schema(description = "Street", example = "Bahnhofstrasse 12")
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String street;

  @Schema(description = "ZIP Code", example = "3000")
  @Min(1000)
  @Max(99999)
  private Integer zip;

  @Schema(description = "City", example = "Bern")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String city;

}
