package ch.sbb.atlas.api.timetable.hearing.model;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "UpdateHearingCanton")
public class UpdateHearingCantonModel extends BaseUpdateHearingModel {

  @Size(max = AtlasFieldLengths.LENGTH_280)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Schema(description = "Statement comment", example = "I am changing my statement from the canton Geneva to the canton Bern.")
  private String comment;
}
