package ch.sbb.workflow.module.sepodi.hearing.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.module.sepodi.hearing.enity.DecisionType;
import ch.sbb.workflow.module.sepodi.hearing.enity.JudgementType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "StopPointPerson")
@Redacted
public class StopPointClientPersonModel {

  @Schema(description = "Technical id", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Firstname", example = "John")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Redacted
  private String firstName;

  @Schema(description = "Lastname", example = "Doe")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Redacted
  private String lastName;

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

  @Schema(description = "Current judgement of the person regarding the workflow", accessMode = AccessMode.READ_ONLY)
  private JudgementType judgement;

  @Schema(description = "Decision Type", accessMode = AccessMode.READ_ONLY)
  private DecisionType decisionType;

  @Schema(description = "Organisation", example = "ZVV Zürcher Verkehrsverbund")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String organisation;

  @Schema(description = "Person Function", example = "Officer")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String personFunction;

  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Schema(description = "mail", example = "mail@sbb.ch")
  @NotBlank
  @Redacted
  private String mail;

  private boolean defaultExaminant;

}
