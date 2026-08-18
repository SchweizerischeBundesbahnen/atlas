package ch.sbb.atlas.api.workflow.tth.dossier;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.AuditableVersionModel;
import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.kafka.model.SwissCanton;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class TthDossierModel extends AuditableVersionModel implements StatementDossierLinked, CantonAssociated {

  @Schema(description = "Generated DB id", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Canton of the dossier", example = "BERN")
  @NotNull
  private SwissCanton swissCanton;

  @Schema(description = "Topic of the dossier", example = "Increase frequency on line S1 during peak hours")
  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String topic;

  @Schema(description = "Status of the dossier")
  private DossierStatus dossierStatus;

  @Schema(description = "Internal justification for documentation purposes", example = "The transport company requires "
      + "additional funds, which will be discussed next year.")
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String internalComment;

  @Schema(description = "Public justification for respondents", example = "The increased frequency is not feasible for the "
      + "transport company.")
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String publicComment;

  @Schema(description = "Statements grouped by the dossier")
  @NotEmpty
  private List<Long> statementIds;

  @Schema(description = "Mail of the business partner at the transport company", example = "urs@tu.ch")
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String boContactMail;

  @Schema(description = "Deadline for the dossier to be answered")
  private LocalDate boDeadlineToAnswer;

  @NotNull
  @Builder.Default
  @Schema(description = "Questions for the BO to answer")
  private List<@Valid TthDossierQuestionModel> questions = new ArrayList<>();

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

}
