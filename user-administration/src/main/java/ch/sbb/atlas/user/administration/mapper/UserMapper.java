package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import com.microsoft.graph.models.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

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

  public static UserAdministrationModel toKafkaModel(UserModel userModel) {
    Set<UserAdministrationPermissionModel> permissionModels = userModel.getPermissions().stream()
        .map(
            permission -> UserAdministrationPermissionModel.builder()
                .application(permission.getApplication())
                .role(permission.getRole())
                .sboids(new HashSet<>(permission.getSboids()))
                .swissCantons(new HashSet<>(permission.getSwissCantons()))
                .build())
        .collect(Collectors.toSet());
    return UserAdministrationModel.builder()
        .sbbUserId(userModel.getSbbUserId())
        .permissions(permissionModels)
        .build();
  }

}
