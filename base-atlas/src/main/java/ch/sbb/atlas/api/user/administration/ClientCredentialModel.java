package ch.sbb.atlas.api.user.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Builder
@Data
@FieldNameConstants
@Schema(name = "ClientCredential")
public class ClientCredentialModel {

  @Schema(description = "Client Id", example = "18746f30-7978-48b5-b19b-0f871fb12e67")
  private String clientCredentialId;

  @Schema(description = "Alias for the Client", example = "öV-info")
  private String alias;

  @Schema(description = "Additional information about the client", example = "Write client for öV-info")
  private String comment;

  @Schema(description = "User permissions")
  private Set<PermissionModel> permissions;

}
