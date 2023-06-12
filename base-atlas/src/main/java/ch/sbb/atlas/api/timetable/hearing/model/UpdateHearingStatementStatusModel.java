package ch.sbb.atlas.api.timetable.hearing.model;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Schema(name = "UpdateHearingStatementStatus")
public class UpdateHearingStatementStatusModel extends BaseUpdateHearingModel {

  @NotNull
  @Schema(description = "Current status")
  private StatementStatus statementStatus;

  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Schema(description = "Statement of Federal office of transport", example = "We can absolutely do that.")
  private String justification;

}
