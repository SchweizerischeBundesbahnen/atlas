package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ManualMailOverride")
public class ManualMailOverrideModel {

  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Schema(description = "Manually maintained E-Mail address override of Azure mails", example = "example@sbb.ch")
  private String mail;

}
