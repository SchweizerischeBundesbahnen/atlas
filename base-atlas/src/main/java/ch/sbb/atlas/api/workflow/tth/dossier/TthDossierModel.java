package ch.sbb.atlas.api.workflow.tth.dossier;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.AuditableVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
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
@Schema(name = "TthDossier")
public class TthDossierModel extends AuditableVersionModel {

  @Schema(description = "Generated DB id", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Topic of the dossier", example = "Increase frequency on line S1 during peak hours")
  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String topic;

  @Schema(description = "Status of the dossier")
  private DossierStatus dossierStatus;

  @Schema(description = "Internal justification for documentation purposes", example = "The transport company requires "
      + "additional funds, which will be discussed next year.")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_5000)
  private String internalComment;

  @Schema(description = "Public justification for respondents", example = "The increased frequency is not feasible for the "
      + "transport company.")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_5000)
  private String publicComment;

  @Schema(description = "Statements grouped by the dossier")
  @NotEmpty
  private List<Long> statementIds;

  @Schema(description = "Mail of the business partner at the transport company")
  @NotBlank
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String boContactMail;

  @Schema(description = "Deadline for the dossier to be answered")
  @NotNull
  private LocalDate boDeadlineToAnswer;

}
