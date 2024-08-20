package ch.sbb.atlas.api.workflow;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class BasePersonModel {

  @Schema(description = "Firstname", example = "John")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String firstName;

  @Schema(description = "Second", example = "Doe")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String lastName;

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

}
