package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import java.util.HashSet;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionCreateMapper {

  public static List<UserPermission> toEntityList(UserPermissionCreateModel userPermissionCreateModel) {
    return userPermissionCreateModel.getPermissions().stream().map(permission -> UserPermission.builder()
        .sbbUserId(userPermissionCreateModel.getSbbUserId().toLowerCase())
        .application(permission.getApplication())
        .role(permission.getRole())
        .sboid(new HashSet<>(permission.getSboids()))
        .build()
    ).toList();
  }
}
