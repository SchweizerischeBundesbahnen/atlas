package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Schema(name = "StatementSender")
public class StatementSenderModel {

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String firstName;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String lastName;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String organisation;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String street;

  @Min(1000)
  @Max(99999)
  private Integer zip;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String city;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  private String email;

}
