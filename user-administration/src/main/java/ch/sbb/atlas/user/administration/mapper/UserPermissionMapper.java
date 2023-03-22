package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserPermissionModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionMapper {

  public static UserPermissionModel toModel(UserPermission userPermission) {
    return UserPermissionModel.builder()
        .role(userPermission.getRole())
        .application(userPermission.getApplication())
        .permissionRestrictions(
            userPermission.getPermissionRestrictions().stream().map(PermissionRestrictionMapper::toModel).toList())
        .editor(userPermission.getEditor())
        .editionDate(userPermission.getEditionDate())
        .creator(userPermission.getCreator())
        .creationDate(userPermission.getCreationDate())
        .build();
  }

  public static UserPermission toEntity(String sbbUserId, UserPermissionModel userPermissionModel) {
    UserPermission userPermission = UserPermission.builder()
        .sbbUserId(sbbUserId)
        .role(userPermissionModel.getRole())
        .application(userPermissionModel.getApplication())
        .build();
    userPermission.setPermissionRestrictions(
        userPermissionModel.getPermissionRestrictions().stream()
            .map(restriction -> PermissionRestrictionMapper.toEntity(userPermission, restriction))
            .collect(Collectors.toSet()));
    return userPermission;
  }

}
