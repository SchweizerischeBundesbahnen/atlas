package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(name = "OtpRequest")
public class OtpRequestModel {

  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  private String examinantMail;

}
