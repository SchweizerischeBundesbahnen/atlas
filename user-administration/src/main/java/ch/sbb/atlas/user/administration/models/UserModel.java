package ch.sbb.atlas.user.administration.models;

import ch.sbb.atlas.user.administration.enumeration.UserAccountStatus;
import com.microsoft.graph.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@Schema(name = "UserModel")
public class UserModel {

  @Schema(description = "SBB User Id", example = "u111111")
  private String sbbUserId;

  @Schema(description = "User lastname")
  private String lastName;

  @Schema(description = "User firstname")
  private String firstName;

  @Schema(description = "User E-Mail address", example = "example@sbb.ch")
  private String mail;

  @Schema(description = "User display name (azure)", example = "Example User (IT-PTR-CEN2-YPT)")
  private String displayName;

  @Schema(description = "User account status", example = "ACTIVE")
  private UserAccountStatus accountStatus;

  @Schema(description = "User permissions")
  private Set<UserPermissionModel> permissions;

  public static UserModel userToModel(User user) {
    return UserModel.builder()
                    .sbbUserId(user.onPremisesSamAccountName)
                    .lastName(user.surname)
                    .firstName(user.givenName)
                    .mail(user.mail)
                    .accountStatus(
                        UserAccountStatus.getUserAccountStatusFromBoolean(user.accountEnabled))
                    .displayName(user.displayName)
                    .permissions(Collections.emptySet())
                    .build();
  }

  @Override
  public boolean equals(final Object userModel) {
    if (this == userModel) {
      return true;
    }
    if (userModel == null) {
      return false;
    }
    if (userModel instanceof UserModel model) {
      return Objects.equals(getSbbUserId(), model.getSbbUserId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 1;
  }

}
