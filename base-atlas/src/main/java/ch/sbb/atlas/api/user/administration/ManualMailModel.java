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
@Schema(name = "ManualMail")
public class ManualMailModel {

  @Email
  @Schema(description = "Manually maintained E-Mail address. An empty/blank value removes the "
      + "override so the Azure E-Mail address applies again.", example = "example@sbb.ch")
  private String mail;

}
