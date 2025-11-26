package ch.sbb.atlas.api.workflow.tth.dossier;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.AuditableVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "TthDossierQuestion")
public class TthDossierQuestionModel extends AuditableVersionModel {

  @Schema(description = "Generated DB id", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Question for the BO to answer", example = "Can the frequency on line S1 be increased during peak hours?")
  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String question;

  @Schema(description = "Answer from the BO to the canton", example = "Yes, the frequency on line S1 can be increased during "
      + "peak hours.")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_5000)
  private String answerToCanton;
}
