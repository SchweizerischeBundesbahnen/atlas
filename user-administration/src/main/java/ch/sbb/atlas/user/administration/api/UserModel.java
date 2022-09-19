package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.user.administration.enumeration.UserAccountStatus;
import com.microsoft.graph.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
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

  public UserAdministrationModel toKafkaModel() {
    Set<UserAdministrationPermissionModel> permissionModels = getPermissions().stream()
                                                                              .map(
                                                                                  permission -> UserAdministrationPermissionModel.builder()
                                                                                                                                 .application(
                                                                                                                                     permission.application())
                                                                                                                                 .role(
                                                                                                                                     permission.role())
                                                                                                                                 .sboids(
                                                                                                                                     permission.sboids())
                                                                                                                                 .build())
                                                                              .collect(
                                                                                  Collectors.toSet());
    return UserAdministrationModel.builder()
                                  .sbbUserId(getSbbUserId())
                                  .permissions(permissionModels)
                                  .build();
  }

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

}


