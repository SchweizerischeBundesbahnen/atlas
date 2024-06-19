package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(name = "OtpVerification")
public class OtpVerificationModel {

  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  private String examinantMail;

  @NotNull
  private String pinCode;

}
