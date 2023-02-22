package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Builder
@Data
@FieldNameConstants
@Schema(name = "User")
public class UserModel {

  @Schema(description = "SBB User Id", example = "u111111")
  private String sbbUserId;

  @Schema(description = "User lastname", example = "Mustermann")
  private String lastName;

  @Schema(description = "User firstname", example = "Max")
  private String firstName;

  @Schema(description = "User E-Mail address", example = "example@sbb.ch")
  private String mail;

  @Schema(description = "User display name (azure)", example = "Example User (IT-PTR-CEN2-YPT)")
  private String displayName;

  @Schema(description = "User account status", example = "ACTIVE")
  private UserAccountStatus accountStatus;

  @Schema(description = "User permissions")
  private Set<UserPermissionVersionModel> permissions;

}
