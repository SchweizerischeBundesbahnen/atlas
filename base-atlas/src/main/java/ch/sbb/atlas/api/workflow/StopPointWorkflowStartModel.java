package ch.sbb.atlas.api.workflow;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "StopPointWorkflowStart")
public class StopPointWorkflowStartModel {

  @Schema(description = "Service Point version id")
  @NotNull
  private Long versionId;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String sloid;

  @NotNull
  @Schema(description = "Swiss Business Organisation ID (SBOID)", example = "ch:1:sboid:100052")
  private String sboid;

  @NotNull
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_30)
  @Schema(description = "Official designation of a location that must be used by all recipients"
      , example = "Biel/Bienne Bözingenfeld/Champ", maxLength = 30)
  private String designationOfficial;

  @Schema(description = "SwissMunicipalityName the location is in", example = "Biel/Bienne")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissMunicipalityName;

  @Schema(description = "List of cc emails for status of hearing")
  private List<String> mails;

  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "Hearing reasons")
  private String workflowComment;

  @Schema(description = "List hearing axamiannts")
  @Valid
  private List<ClientPersonModel> examinants;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;
}
