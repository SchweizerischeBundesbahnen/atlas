package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
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
@Schema(name = "TimetableHearingStatementSenderV2")
public class TimetableHearingStatementSenderModelV2 extends TimetableHearingStatementSenderModel {

  @Schema(description = "E-Mail addresses")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10, message = "Minimum 1 email address is required and maximum 10 email "
      + "addresses are allowed")
  private Set<@Size(max = AtlasFieldLengths.LENGTH_100) @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS) String> emails = new HashSet<>();

}
