package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Redacted
@Schema(name = "TerminationDecision")
public class TerminationDecisionModel {

  @Schema(description = "Judgement")
  @NotNull
  private JudgementType judgement;

  @Schema(description = "Motivation", example = "I agree")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  private String motivation;

  @Schema(description = "Decision by person: Info+ or NOVA")
  @NotNull
  private TerminationDecisionPerson terminationDecisionPerson;

  @NotNull
  @Schema(description = "Termination Date ")
  private LocalDate terminationDate;

  @Redacted
  @Schema(description = "Firstname", example = "John")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String firstName;

  @Redacted
  @Schema(description = "Second", example = "Doe")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String lastName;

  @Schema(description = "Organisation", example = "ZVV Zürcher Verkehrsverbund")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String organisation;

  @Redacted(showFirstChar = true)
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  private String examinantMail;

}
