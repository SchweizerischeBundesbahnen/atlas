package ch.sbb.atlas.api.workflow;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.validation.TrimmedNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
public abstract class BasePersonModel {

  @Schema(description = "Firstname", example = "John")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Redacted
  private String firstName;

  @Schema(description = "Lastname", example = "Doe")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Redacted
  private String lastName;

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

}