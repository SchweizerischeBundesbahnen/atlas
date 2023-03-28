package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Schema(name = "ClientCredentialPermissionCreate")
public class ClientCredentialPermissionCreateModel extends PermissionCreateModel {

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Schema(description = "Client Id", example = "18746f30-7978-48b5-b19b-0f871fb12e67")
  private String clientCredentialId;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_100)
  @Schema(description = "Alias for the Client", example = "öV-info")
  private String alias;

  @Size(max = AtlasFieldLengths.LENGTH_100)
  @Schema(description = "Additional information about the client", example = "Write client for öV-info")
  private String comment;

}
