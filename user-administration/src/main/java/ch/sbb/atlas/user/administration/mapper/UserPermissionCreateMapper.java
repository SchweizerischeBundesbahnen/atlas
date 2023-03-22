package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionCreateMapper {

  public static List<UserPermission> toEntityList(UserPermissionCreateModel userPermissionCreateModel) {
    return userPermissionCreateModel.getPermissions().stream().map(permission -> UserPermission.builder()
            .sbbUserId(userPermissionCreateModel.getSbbUserId().toLowerCase())
            .application(permission.getApplication())
            .role(permission.getRole())
            .permissionRestrictions(
                permission.getPermissionRestrictions().stream().map(restriction ->
                        PermissionRestriction.builder()
                            .type(restriction.getType())
                            .restriction(restriction.getValueAsString())
                            .build())
                    .collect(Collectors.toSet()))
        .build()
    ).toList();
  }
}
