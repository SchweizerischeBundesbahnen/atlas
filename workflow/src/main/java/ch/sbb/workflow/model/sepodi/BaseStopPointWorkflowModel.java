package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class BaseStopPointWorkflowModel {

  @Schema(description = "Workflow id", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Service Point version id")
  @NotNull
  private Long versionId;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String sloid;

  @Schema(description = "List of cc emails for status of hearing")
  private List<String> ccEmails;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "Hearing reasons")
  private String workflowComment;

  @Schema(description = "List hearing examinants")
  private List<@Valid StopPointClientPersonModel> examinants;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  private WorkflowStatus status;

  @Schema(description = "Workflow created at")
  private LocalDateTime createdAt;

  @Schema(description = "Workflow valid from")
  private LocalDate validFrom;

}
