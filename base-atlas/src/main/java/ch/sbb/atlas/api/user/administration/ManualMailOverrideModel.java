package ch.sbb.atlas.api.user.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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

  @Email
  @Schema(description = "Manually maintained E-Mail address override of Azure mails", example = "example@sbb.ch")
  private String mail;

}
