package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@Schema(name = "AddExaminants")
public class AddExaminantsModel {

  @NotNull
  @Schema(description = "Additional examinants")
  private List<StopPointClientPersonModel> examinants;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Schema(description = "Additional mails to cc")
  private List<@Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS) @Size(max = AtlasFieldLengths.LENGTH_100) String> ccEmails;

}
