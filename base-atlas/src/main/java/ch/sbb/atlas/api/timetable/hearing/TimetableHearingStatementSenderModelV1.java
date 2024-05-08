package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
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
@Schema(name = "TimetableHearingStatementSender", deprecated = true)
public class TimetableHearingStatementSenderModelV1 extends TimetableHearingStatementSenderModel {

  @Schema(description = "E-Mail address", example = "maurer@post.ch")
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  private String email;

}
