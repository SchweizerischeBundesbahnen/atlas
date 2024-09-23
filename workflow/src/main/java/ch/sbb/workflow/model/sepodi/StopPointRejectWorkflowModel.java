package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.workflow.BasePersonModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(name = "StopPointRejectWorkflow")
public class StopPointRejectWorkflowModel extends BasePersonModel {

  @Schema(description = "Organisation", example = "ZVV Zürcher Verkehrsverbund")
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String organisation;

  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Schema(description = "mail", example = "mail@sbb.ch")
  @NotBlank
  private String mail;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "Reject motivation")
  private String motivationComment;

}
