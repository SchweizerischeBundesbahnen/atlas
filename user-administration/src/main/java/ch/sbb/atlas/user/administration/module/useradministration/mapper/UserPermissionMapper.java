package ch.sbb.atlas.user.administration.module.useradministration.mapper;

import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.user.administration.mapper.PermissionRestrictionMapper;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionMapper {

  public static PermissionModel toModel(UserPermission userPermission) {
    return PermissionModel.builder()
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

  public static UserPermission toEntity(String sbbUserId, PermissionModel permissionModel) {
    UserPermission userPermission = UserPermission.builder()
        .sbbUserId(sbbUserId)
        .role(permissionModel.getRole())
        .application(permissionModel.getApplication())
        .build();
    userPermission.setPermissionRestrictions(
        permissionModel.getPermissionRestrictions().stream()
            .map(restriction -> PermissionRestrictionMapper.toEntity(userPermission, restriction))
            .collect(Collectors.toSet()));
    return userPermission;
  }

}
