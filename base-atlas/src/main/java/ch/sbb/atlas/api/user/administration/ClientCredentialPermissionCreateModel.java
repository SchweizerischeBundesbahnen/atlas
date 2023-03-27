package ch.sbb.atlas.api.user.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Schema(name = "UserPermissionCreate")
public class ClientCredentialPermissionCreateModel extends PermissionCreateModel {

  @NotBlank
  @Schema(description = "Client Id", example = "18746f30-7978-48b5-b19b-0f871fb12e67")
  private String clientCredentialId;

  @NotBlank
  @Schema(description = "Alias for the Client", example = "öV-info")
  private String alias;

  @Schema(description = "Additional information about the client", example = "Write client for öV-info")
  private String comment;

}
